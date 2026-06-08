# E-learning Platform

## Cấu trúc thư mục (Packages)
Dự án được phân chia theo chuẩn:
- `controller`: Chứa các REST API controllers (`AuthController`, `StudyController`).
- `service`: Chứa logic nghiệp vụ (`AuthService`, `StudyService`).
- `repository`: Chứa các Spring Data JPA repositories.
- `security`: Chứa cấu hình bảo mật, `JwtService`, và `JwtAuthenticationFilter`.
- `client`: Chứa Feign client (`CertificateClient`) tích hợp với API ngoài.
- `model`: Chứa các Entity mapping với CSDL.
- `dto`: Chứa các class Data Transfer Object.
- `exception`: Chứa custom exceptions (ví dụ: `CourseNotCompletedException`).

## Giải thích cơ chế chặn token đã bị đăng xuất ở tầng Filter

Ở tầng Filter (`JwtAuthenticationFilter`), để chặn các token đã bị thu hồi (đăng xuất) truy cập vào các API được bảo vệ, hệ thống thực hiện các bước sau:
1. **Trích xuất Access Token**: Lấy chuỗi JWT từ header `Authorization`.
2. **Kiểm tra token trong CSDL**: Filter sẽ thực hiện một truy vấn DB (sử dụng `StudentTokenRepository.findByTokenString(jwt)`) để tìm bản ghi lưu trữ thông tin của token này.
3. **Kiểm tra trạng thái cờ `isRevoked` và `isExpired`**: 
   - Nếu bản ghi token được tìm thấy có trạng thái `isRevoked = true` (tức là đã bị người dùng gọi API logout trước đó) hoặc `isExpired = true`, filter sẽ đánh giá token này là không hợp lệ.
   - Quá trình này hoàn toàn dùng thông tin trên DB để quyết định tính xác thực bên cạnh việc kiểm tra chữ ký (signature) của chính chuỗi JWT đó (nhằm đảm bảo token không bị làm giả).
4. **Cấp quyền truy cập**: Chỉ khi token còn hiệu lực cả về mặt toán học (chữ ký hợp lệ, chưa hết hạn do thời gian phát hành) và về mặt logic hệ thống (tồn tại trong DB và chưa bị revoke), Filter mới thiết lập `SecurityContext`, cho phép yêu cầu tiếp tục đi vào các controller (`StudyController`). Nếu token đã bị thu hồi, Filter sẽ bỏ qua việc xác thực và request sẽ bị Spring Security chặn lại (trả về lỗi 403/401).