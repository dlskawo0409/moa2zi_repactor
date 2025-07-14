from flask import Flask, request, jsonify
import joblib
import re

app = Flask(__name__)

# 모델 및 벡터라이저 로드
vectorizer = joblib.load("vectorizer.pkl")
le = joblib.load('label_encoder.pkl')
model = joblib.load("sgd_model.pkl")

@app.route("/predict", methods=["POST"])
def predict():
    data = request.get_json()
    user_input = data.get("text")

    input = clean_text(remove_stopwords(user_input))

    if not user_input:
        return jsonify({"error": "No input text provided"}), 400

    # 텍스트 벡터화 및 예측
    X_input = vectorizer.transform([input])
    prediction = model.predict(X_input)[0]

    predicted_label = le.inverse_transform([prediction])   # 예: 1 → 'dog'


    return jsonify({"prediction": predicted_label[0]})


def clean_text(text):
    return re.sub(r'[^가-힣0-9\s]', '', text)


STOPWORDS = ['주식회사', '유한회사', '더', '㈜', '코리아', '엔터', '컴퍼니', '인터내셔널']


def remove_stopwords(text):
    for stopword in STOPWORDS:
        text = text.replace(stopword, '')
    return text

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000)
