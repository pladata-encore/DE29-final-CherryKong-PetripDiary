import os
import numpy as np
import pandas as pd
from PIL import Image
from tensorflow.keras.applications.resnet50 import ResNet50, preprocess_input
from tensorflow.keras.preprocessing import image
from scipy.spatial.distance import cosine

class ImageSimilarity:
    def __init__(self, embeddings, merge_df):
        self.embeddings = embeddings
        self.merge_df = merge_df
        self.model = ResNet50(weights='imagenet', include_top=False, pooling='avg')
        self.model.trainable = False

    def get_embedding(self, image_path):
        img = image.load_img(image_path, target_size=(224, 224))
        img_array = image.img_to_array(img)
        img_array = np.expand_dims(img_array, axis=0)
        img_array = preprocess_input(img_array)
        embedding = self.model.predict(img_array).flatten()
        return embedding / np.linalg.norm(embedding)

    def compare_similarity(self, input_image_path):
        input_embedding = self.get_embedding(input_image_path)
        similarities = {img_path: 1 - cosine(input_embedding, emb) for img_path, emb in self.embeddings.items()}
        return similarities

    def find_info(self, img_path):
        # Assuming the name is the basename of the image path without the extension
        return os.path.basename(img_path).split('.')[0]

    def get_top_results(self, similarities, merge_df, num_results=8):
        sorted_similarities = sorted(similarities.items(), key=lambda item: item[1], reverse=True)
        print(sorted_similarities)

        similar_places = merge_df[merge_df['name'].isin([self.find_info(img_path) for img_path, _ in sorted_similarities[:num_results]])]
        print(similar_places)
        
        results = []
        for idx, place in similar_places.iterrows():
            results.append({
                'name': place.get('name', 'No Name'),
                'address': place.get('detail_address', 'No Address'),
                'category': place.get('info1_gong_gong', 'No Keyword'),
                'short_info': place.get('short_info', 'No Information')
            })

        return results