package hei.school.library.endpoint.rest.controller.library;
import hei.school.library.dto.genre.GenreRequest;
import hei.school.library.exception.BadRequestException;
import hei.school.library.exception.NotFoundException;
import hei.school.library.service.GenreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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
        }catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
    @GetMapping("/{genreId}")
    public ResponseEntity<?> getGenreById(
            @PathVariable UUID genreId
    ) {
        try{
            return ResponseEntity.ok(genreService.getGenreById(genreId));
        }catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }catch (NotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

    }

}
