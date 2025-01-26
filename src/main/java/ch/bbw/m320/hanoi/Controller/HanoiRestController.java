package ch.bbw.m320.hanoi.Controller;

import ch.bbw.m320.hanoi.HanoiBoard;
import ch.bbw.m320.hanoi.Service.HanoiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/hanoi")
public class HanoiRestController {

    private final HanoiService hanoiService;

    public HanoiRestController(HanoiService hanoiService) {
        this.hanoiService = hanoiService;
    }

    @DeleteMapping
    public HanoiBoard reset(@RequestParam(required = false, defaultValue = "3") int size) {
        return hanoiService.reset(size);
    }

    @GetMapping
    public HanoiBoard getHanoi(){
        return hanoiService.getHanoiBoard();
    }

    @PostMapping
    public HanoiBoard move(@RequestBody HanoiBoard.Move move){
        return hanoiService.move(move);
    }

    @GetMapping("/hint")
    public HanoiBoard.Move getHint(){
        return hanoiService.getHint();
    }

}