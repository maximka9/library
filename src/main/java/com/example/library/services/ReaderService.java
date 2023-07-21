package com.example.library.services;

import com.example.library.models.Reader;
import com.example.library.models.enums.Role;
import com.example.library.repositories.ReaderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ReaderService {
    private final ReaderRepository readerRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ReaderService(ReaderRepository readerRepository, PasswordEncoder passwordEncoder) {
        this.readerRepository = readerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean createReader(Reader reader) {
        String email = reader.getEmail();
        if (readerRepository.findByEmail(email) != null) return false;
        reader.setPassword(passwordEncoder.encode(reader.getPassword()));
        reader.getRoles().add(Role.ROLE_USER);
        log.info("Saving new User with username: {}", reader.getEmail());
        readerRepository.save(reader);
        return true;
    }

    public Optional<Reader> getReaderById(Long id) {
        return readerRepository.findById(id);
    }

    public List<Reader> getAllReaders() {
        return readerRepository.findByArchivedFalse();
    }

    public Reader updateReader(Long id, Reader updatedReader) {
        Optional<Reader> optionalReader = readerRepository.findById(id);
        if (optionalReader.isPresent()) {
            Reader existingReader = optionalReader.get();

            // Проверка, было ли передано имя для обновления
            if (updatedReader.getName() != null) {
                existingReader.setName(updatedReader.getName());
            }

            // Проверка, был ли передан email для обновления
            if (updatedReader.getEmail() != null) {
                existingReader.setEmail(updatedReader.getEmail());
            }

            // Добавьте другие свойства для обновления, если они есть в вашей модели Reader.

            return readerRepository.save(existingReader);
        } else {
            // Читатель с указанным ID не найден, можете обработать эту ситуацию, если нужно.
            return null;
        }
    }

    public boolean archiveReaderById(Long id) {
        Optional<Reader> optionalReader = readerRepository.findById(id);
        if (optionalReader.isPresent()) {
            Reader reader = optionalReader.get();
            if (!reader.isArchived() && !reader.getRoles().contains(Role.ROLE_ADMIN)) {
                // Проверка, что читатель еще не архивирован и не является администратором
                reader.setArchived(true);
                readerRepository.save(reader);
                return true;
            } else {
                return false;
                // Читатель уже архивирован или является администратором
            }
        } else {
            return false;
            // Читатель с указанным ID не найден
        }
    }

    public boolean authenticateReader(String email, String password) {
// Находим пользователя по email в базе данных
        Reader reader = readerRepository.findByEmail(email);

        // Проверяем, что пользователь с таким email найден и его пароль совпадает с переданным паролем
        if (reader != null && passwordEncoder.matches(password, reader.getPassword())) {
            return true; // Аутентификация прошла успешно
        } else {
            return false; // Аутентификация не удалась
        }
    }
    public Long getCurrentUserId(String email) {
        Reader reader = readerRepository.findByEmail(email);
        if (reader != null) {
            return reader.getId();
        }
        return null;
    }

    public Reader getReaderByEmail(String email) {
        return readerRepository.findByEmail(email);
    }

}