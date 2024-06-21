FROM python:3.9

COPY /* /final

WORKDIR /final

# 필요한 패키지 및 도구 설치
RUN apt-get update && apt-get install -y curl && \
    curl https://sh.rustup.rs -sSf | sh -s -- -y

    
# rust와 cargo가 PATH에 추가되도록 설정
ENV PATH="/root/.cargo/bin:${PATH}"

RUN pip install --upgrade pip

RUN pip install --no-cache-dir --upgrade -r /final/requirements.txt --use-deprecated=legacy-resolver

# COPY ./app /final/app

CMD ["uvicorn", "app:app", "--host", "0.0.0.0", "--port", "8000"]