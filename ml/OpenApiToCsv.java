import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class OpenApiToCsv {

    public static void main(String[] args) {
        String currentDir = System.getProperty("user.dir");
        String subDir = currentDir + File.separator + "ml";

        String csvFilePath = subDir + File.separator + "data.csv";
        String progressFilePath = subDir + File.separator + "progress.txt";

        int[] progress = loadLastProgress(progressFilePath);
        int year = progress[0];
        int pageNo = progress[1];
        int numOfRows = 10000;

        String serviceKey = "serviceKey";

        while (year >= 2000) { // 원하는 최소 연도까지 반복
            try {
                String baseUrl = "https://apis.data.go.kr/1130000/FftcBrandFrcsStatsService/getBrandFrcsStats";
                String encodedKey = URLEncoder.encode(serviceKey, StandardCharsets.UTF_8.toString());

                String urlStr = baseUrl
                        + "?serviceKey=" + encodedKey
                        + "&pageNo=" + pageNo
                        + "&numOfRows=" + numOfRows
                        + "&resultType=json"
                        + "&yr=" + year;

                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-type", "application/json");

                int responseCode = conn.getResponseCode();
                System.out.println("📦 요청 년도: " + year + " | 페이지: " + pageNo + " | 응답 코드: " + responseCode);

                if (responseCode == 200) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                    br.close();

                    JSONObject json = new JSONObject(response.toString());

                    JSONArray items = json.getJSONArray("items");

                    if (items.length() == 0) {
                        System.out.println("✅ " + year + "년 데이터 완료");
                        year--;           // 다음 연도 처리
                        pageNo = 1;       // 페이지 초기화
                        continue;
                    }

                    appendToCsv(csvFilePath, items);
                    saveProgress(progressFilePath, year, pageNo);
                    pageNo++;

                } else {
                    System.out.println("❌ 실패: " + conn.getResponseMessage());
                    break;
                }

                conn.disconnect();
                Thread.sleep(500);

            } catch (Exception e) {
                System.out.println("❌ 예외 발생: " + e.getMessage());
                e.printStackTrace();
                break;
            }
        }

        System.out.println("✅ 전체 연도 데이터 수집 완료 또는 중단됨");
    }


    private static void appendToCsv(String filePath, JSONArray items) {
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filePath, true), StandardCharsets.UTF_8))) {

            for (int i = 0; i < items.length(); i++) {
                JSONObject obj = items.getJSONObject(i);
                String lineCSV = String.join(",",
                        obj.getString("yr"),
                        obj.getString("indutyLclasNm"),
                        obj.getString("indutyMlsfcNm"),
                        obj.getString("corpNm"),
                        obj.getString("brandNm").trim(),
                        String.valueOf(obj.getInt("frcsCnt")),
                        String.valueOf(obj.getInt("newFrcsRgsCnt")),
                        String.valueOf(obj.getInt("ctrtEndCnt")),
                        String.valueOf(obj.getInt("ctrtCncltnCnt")),
                        String.valueOf(obj.getInt("nmChgCnt")),
                        String.valueOf(obj.getInt("avrgSlsAmt")),
                        String.valueOf(obj.getInt("arUnitAvrgSlsAmt"))
                );
                bw.write(lineCSV);
                bw.newLine();
            }
            System.out.println("📁 저장 완료 (UTF-8): " + filePath);
        } catch (IOException | JSONException e) {
            System.out.println("❌ CSV 파일 쓰기 실패: " + e.getMessage());
        }
    }


    private static int loadLastPageNo(String progressFilePath) {
        File file = new File(progressFilePath);
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                return Integer.parseInt(br.readLine()) + 1;
            } catch (IOException | NumberFormatException e) {
                System.out.println("⚠️ 진행 상태 로딩 실패. 처음부터 시작합니다.");
            }
        }
        return 1;
    }

    private static void saveProgress(String progressFilePath, int pageNo) {
        try (FileWriter fw = new FileWriter(progressFilePath, false)) {
            fw.write(String.valueOf(pageNo));
        } catch (IOException e) {
            System.out.println("⚠️ 진행 상태 저장 실패");
        }
    }

    private static int[] loadLastProgress(String progressFilePath) {
        File file = new File(progressFilePath);
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String[] parts = br.readLine().split(":");
                int lastYear = Integer.parseInt(parts[0]);
                int lastPage = Integer.parseInt(parts[1]);
                return new int[]{lastYear, lastPage + 1}; // 다음 페이지부터 시작
            } catch (IOException | NumberFormatException e) {
                System.out.println("⚠️ 진행 상태 로딩 실패. 처음부터 시작합니다.");
            }
        }
        return new int[]{2024, 1}; // 기본 시작값
    }

    private static void saveProgress(String progressFilePath, int year, int pageNo) {
        try (FileWriter fw = new FileWriter(progressFilePath, false)) {
            fw.write(year + ":" + pageNo);
        } catch (IOException e) {
            System.out.println("⚠️ 진행 상태 저장 실패");
        }
    }

}
