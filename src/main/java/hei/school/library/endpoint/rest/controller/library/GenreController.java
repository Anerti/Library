package hei.school.library.endpoint.rest.controller.library;
import hei.school.library.dto.genre.GenreRequest;
import hei.school.library.service.GenreService;
import jakarta.ws.rs.BadRequestException;
import org.hibernate.query.SemanticException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/genres")
public class GenreController {
    private final GenreService genreService;
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody GenreRequest request) {
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(genreService.createGenreByName(request));
        }catch (SemanticException e) {
            return ResponseEntity.unprocessableEntity().body(e.getMessage());
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}
