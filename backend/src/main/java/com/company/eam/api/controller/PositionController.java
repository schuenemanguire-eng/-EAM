package com.company.eam.api.controller;

import com.company.eam.api.dto.request.PositionRequest;
import com.company.eam.api.dto.response.PositionVO;
import com.company.eam.application.service.PositionAppService;
import com.company.eam.common.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/position")
@Tag(name = "position")
public class PositionController {

    @Autowired
    private PositionAppService positionAppService;

    @GetMapping
    public Result<List<PositionVO>> list() {
        return Result.ok(positionAppService.listPositions());
    }

    @PostMapping
    public Result<Void> create(@Valid @RequestBody PositionRequest req) {
        positionAppService.createPosition(req);
        return Result.ok();
    }

    @PutMapping
    public Result<Void> update(@Valid @RequestBody PositionRequest req) {
        positionAppService.updatePosition(req);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        positionAppService.deletePosition(id);
        return Result.ok();
    }
}
