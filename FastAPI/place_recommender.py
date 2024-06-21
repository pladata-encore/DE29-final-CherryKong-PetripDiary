import pandas as pd
import numpy as np
from sklearn.metrics.pairwise import cosine_similarity
from sklearn.feature_extraction.text import TfidfVectorizer, TfidfTransformer
import random

class PlaceRecommender:
    def __init__(self, text_data, tfidf, vectorizer, gong_gong_df, goosuk_df, merge_df):
        self.text_data = text_data
        self.tfidf = tfidf
        self.vectorizer = vectorizer
        self.gong_gong_df = gong_gong_df
        self.goosuk_df = goosuk_df
        self.merge_df = merge_df
    
    def most_similar(self, query, top = 20):
        query_tfidf = self.vectorizer.transform([query])
        similarities = cosine_similarity(query_tfidf, self.tfidf).flatten()
        related_docs = similarities.argsort()[:-top-1:-1]
        return self.text_data.iloc[related_docs]
    
    def recommend_places(self, query, top=15):
        # Get most similar places
        similar_places = self.most_similar(query, top=top)
        
        # Format results
        results = []
        for idx, place in similar_places.iterrows():
            results.append({
                'name': place.get('name', 'No Name'),
                'address': place.get('detail_address', 'No Address'),
                'category': place.get('info1_gong_gong', 'No Keyword'),
                'short_info': place.get('short_info', 'No Information')
            })
     
        return results

