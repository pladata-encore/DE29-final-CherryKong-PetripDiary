import os
import io
from pydantic import BaseModel
import yaml
import boto3
import pandas as pd
from PIL import Image
from fastapi import FastAPI, File, UploadFile, HTTPException, Form, Request
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
from sqlalchemy import create_engine, text
import asyncio
import pickle
import joblib
from image_recommender import ImageSimilarity
from place_recommender import PlaceRecommender
import random

# FastAPI 애플리케이션 초기화
app = FastAPI()

# CORS 설정
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # 모든 도메인 허용
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

class CurruentAddressRequest(BaseModel):
    currentAddress: str

# Load S3 config
with open("s3config.yml", "r") as ymlfile:
    cfg = yaml.safe_load(ymlfile)

# S3 configuration
s3 = boto3.client(
    service_name=cfg["service_name"],
    region_name=cfg["region_name"],
    aws_access_key_id=cfg["access_key"],
    aws_secret_access_key=cfg["secret_key"]
)
BUCKET_NAME = cfg["bucket_name"]

# 로컬 디렉토리 설정
LOCAL_DIR = "local_data"
os.makedirs(LOCAL_DIR, exist_ok=True)

# S3에서 CSV 파일 로드
def load_csv_from_s3(bucket, key, local_path):
    response = s3.get_object(Bucket=bucket, Key=key)
    local_file_path = os.path.join(LOCAL_DIR, local_path)
    with open(local_file_path, 'wb') as f:
        f.write(response['Body'].read())
    return pd.read_csv(local_file_path)

# S3에서 pkl + joblib 파일 로드
def load_pickle_from_s3(bucket, key, local_path):
    with open(local_path, 'wb') as f:
        f.write(s3.get_object(Bucket=bucket, Key=key)['Body'].read())

# 데이터 로드
gong_gong_df = load_csv_from_s3(BUCKET_NAME, 'recdataset/gong_gong.csv', 'gong_gong.csv')
goosuk_df = load_csv_from_s3(BUCKET_NAME, 'recdataset/goosuk.csv', 'goosuk.csv')
merge_df = load_csv_from_s3(BUCKET_NAME, 'recdataset/merge_df.csv', 'merge_df.csv')

# pickle + joblib 로드
load_pickle_from_s3(BUCKET_NAME, 'weight/images_weight/res_embeddings.pkl', os.path.join(LOCAL_DIR, 'res_embeddings.pkl'))
load_pickle_from_s3(BUCKET_NAME, 'weight/text_weight/text_data.joblib', os.path.join(LOCAL_DIR, 'text_data.joblib'))
load_pickle_from_s3(BUCKET_NAME, 'weight/text_weight/tfidf.joblib', os.path.join(LOCAL_DIR, 'tfidf.joblib'))
load_pickle_from_s3(BUCKET_NAME, 'weight/text_weight/vectorizer.joblib', os.path.join(LOCAL_DIR, 'vectorizer.joblib'))

# Initialize models
with open(os.path.join(LOCAL_DIR, 'res_embeddings.pkl'), 'rb') as f:
    res_embeddings = pickle.load(f)
image_similarity = ImageSimilarity(res_embeddings, merge_df)

text_data = joblib.load(os.path.join(LOCAL_DIR, 'text_data.joblib'))
tfidf = joblib.load(os.path.join(LOCAL_DIR, 'tfidf.joblib'))
vectorizer = joblib.load(os.path.join(LOCAL_DIR, 'vectorizer.joblib'))

place_recommender = PlaceRecommender(text_data, tfidf, vectorizer, gong_gong_df, goosuk_df, merge_df)

# 초기화
img_places = []
text_places = []
lock = asyncio.Lock()

# 이미지 관련 엔드포인트
@app.post("/image_search")
async def search_images(file: UploadFile = File(...)):
    try:
        # 이미지 데이터 읽기
        image_data = await file.read()
        img = Image.open(io.BytesIO(image_data)).convert('RGB')
        file_extension = file.filename.split('.')[-1].lower()
        allowed_extensions = {'jpg', 'jpeg', 'png', 'webp'}
        
        # 지원하지 않는 파일 타입인 경우 예외 처리
        if file_extension not in allowed_extensions:
            raise HTTPException(status_code=400, detail="Unsupported file type")

        # 임시 이미지 파일 경로 설정 및 저장
        temp_image_path = os.path.join(LOCAL_DIR, 'temp_uploaded_image.' + file_extension)
        img.save(temp_image_path)

        # 이미지 유사도 분석 및 결과 반환
        res_similarities = image_similarity.compare_similarity(temp_image_path)
        top_results = image_similarity.get_top_results(res_similarities, merge_df, num_results=8)

        # 결과를 복사하여 반환
        async with lock:
            img_places.clear()
            img_places.extend(top_results)

        return {"places": img_places}

    except HTTPException as e:
        raise e
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# 이미지 검색 결과를 조회하는 엔드포인트
@app.get("/image_search")
async def view_img_places():
    try:
        return {"places": img_places}
    except Exception as e:
        print(f"Exception: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))
        
# Endpoint for searching places
class SearchRequest(BaseModel):
    userID: str
    input_text: str
    city: str

@app.post("/search")
async def search_places(request: SearchRequest):
    try:
        print(f"Request received: {request}")

        top_results = place_recommender.recommend_places(request.input_text)
        print(f"Top results: {top_results}")

        filtered_places = [place for place in top_results if place['address'].startswith(request.city)]

        if len(filtered_places) > 8:
            selected_places = random.sample(filtered_places, 8)
        else:
            selected_places = filtered_places

        async with lock:
            text_places.clear()
            text_places.extend(selected_places)

        return {"results": text_places}

    except HTTPException as e:
        print(f"HTTPException: {e.detail}")
        raise e
    
    except Exception as e:
        print(f"Exception: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/search")
async def view_text_places():
    try:
        return {"places": text_places}
    except Exception as e:
        print(f"Exception: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))
    
DATABASE_URL = ""
engine = create_engine(DATABASE_URL)

# FastAPI 엔드포인트 정의
@app.post("/planning")
async def get_places(request: CurruentAddressRequest):
    city = request.currentAddress
    city = city.replace("\"", "")
    conn = engine.connect()
    search_pattern = f"{city}%"
    query = text("SELECT name, detail_address, short_info FROM merge_df WHERE detail_address LIKE :search_pattern")
    result = conn.execute(query, {"search_pattern": search_pattern})
    places_data = [{"name": row[0], "address": row[1], "short_info": row[2]} for row in result]
    conn.close()
    print(places_data)
    return JSONResponse(content=places_data)

@app.get("/")
async def test():
    return "test success"
# 애플리케이션 실행
if __name__ == '__main__':
    import uvicorn
    uvicorn.run(host="0.0.0.0", port=8000, log_level="info")
