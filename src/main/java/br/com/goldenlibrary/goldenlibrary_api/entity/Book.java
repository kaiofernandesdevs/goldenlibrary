    package br.com.goldenlibrary.goldenlibrary_api.entity;

    import br.com.goldenlibrary.goldenlibrary_api.enums.ReadingStatus;
    import jakarta.validation.constraints.NotBlank;
    import jakarta.validation.constraints.NotNull;
    import org.springframework.data.annotation.Id;
    import org.springframework.data.mongodb.core.mapping.Document;

    import java.time.LocalDateTime;

    @Document(collection = "books")
    public class Book {

        @Id
        private String id;

        private String userId;

        @NotBlank(message = "Titulo é obrigatorio")
        private String title;

        @NotBlank(message = "Nome é obrigatório")
        private String author;

        @NotBlank(message = "Gênero é obrigatorio")
        private String genre;

        @NotNull(message = "Status de leitura é obrigatorio")
        private ReadingStatus status;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;


        public Book() {
            this.createdAt = LocalDateTime.now();
            this.updatedAt = LocalDateTime.now();
        }

        public Book(String id, String userId, String title, String author, String genre, ReadingStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
            this.id = id;
            this.userId = userId;
            this.title = title;
            this.author = author;
            this.genre = genre;
            this.status = status;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getGenre() {
            return genre;
        }

        public void setGenre(String genre) {
            this.genre = genre;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public ReadingStatus getStatus() {
            return status;
        }

        public void setStatus(ReadingStatus status) {
            this.status = status;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public LocalDateTime getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
        }
    }
