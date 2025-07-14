# 베이스 이미지
FROM python:3.9-slim

# 작업 디렉토리 설정
WORKDIR /app

# 필요한 파일 복사
COPY app.py .
COPY label_encoder.pkl .
COPY vectorizer.pkl .
COPY sgd_model.pkl .
COPY requirements.txt .

# 패키지 설치
RUN pip install --no-cache-dir -r requirements.txt

# 포트 열기
EXPOSE 5000

# Flask 앱 실행
CMD ["python", "app.py"]
