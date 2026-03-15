package co.edu.eci.blueprints.rt;

import co.edu.eci.blueprints.dto.*;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.List;

@Controller
public class BlueprintRTController {

  private final SimpMessagingTemplate template;

  public BlueprintRTController(SimpMessagingTemplate template) {
    this.template = template;
  }

  @MessageMapping("/draw")
  public void onDraw(DrawEvent evt) {
    var upd = new BlueprintUpdate(evt.author(), evt.name(), List.of(evt.point()));
    template.convertAndSend("/topic/blueprints." + evt.author() + "." + evt.name(), upd);
  }

  @ResponseBody
  @GetMapping("/api/rt/blueprints/{author}/{name}")
  public BlueprintUpdate get(@PathVariable String author, @PathVariable String name) {
    return new BlueprintUpdate(author, name, List.of(new PointDTO(10,10), new PointDTO(40,50)));
  }
}
