package com.healnexus.audit;

import com.healnexus.model.Role;
import com.healnexus.security.SecurityUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class AuditAspect {
    private final SecurityUtils securityUtils;
    @PostConstruct
    public void init() {
        System.out.println("AuditAspect Loaded");
    }
    @Around("@annotation(audit)")
    public Object logAudit(ProceedingJoinPoint joinPoint, Audit audit) throws Throwable {
        Long startTime = System.currentTimeMillis();
      String methodName=joinPoint.getSignature().getName();
      String action= audit.action();
      String loggedInUser= securityUtils.getCurrentUserEmail();
        Role currentRole=securityUtils.getUserRole();
        Object [] value=joinPoint.getArgs();
        String entityId=value.length>0?value[0].toString():"N/A";


      try {

          Object result = joinPoint.proceed();
          Long duration=System.currentTimeMillis()-startTime;
          log.info(
                  "event=AUDIT status=SUCCESS user={} role={} action={} method={} entityId={} durationMs={} timestamp={}",
                  loggedInUser,
                  currentRole,
                  action,
                  methodName,
                  entityId,
                  duration,
                  LocalDateTime.now()
          );
          return result;
      }catch (Exception e){
          Long duration=System.currentTimeMillis()-startTime;
          log.error(
                  "event=AUDIT status=FAILED user={} role={} action={} method={} entityId={} durationMs={} timestamp={}",
                  loggedInUser,
                  currentRole,
                  action,
                  methodName,
                  entityId,
                  duration,
                  LocalDateTime.now()
          );
          throw e;
      }
    }


}
