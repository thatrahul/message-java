UPDATE ofVersion SET version=5 WHERE name = 'mmxappmgmt';

CREATE TABLE mmxWebHook (
    id          INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    appId       VARCHAR(16)     NOT NULL,
    hookName    VARCHAR(16)     NOT NULL,
    targetURL   VARCHAR(500)    NOT NULL,
    eventType   VARCHAR(50)     NOT NULL,
    eventConfig VARCHAR(500)    NULL,
    dateCreated  datetime     NOT NULL DEFAULT now(),
    dateUpdated  datetime     NULL,
    FOREIGN KEY (appId) REFERENCES mmxApp(appId) ON DELETE CASCADE
);