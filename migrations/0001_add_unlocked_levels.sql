ALTER TABLE player ADD COLUMN unlocked_levels TEXT DEFAULT '';

UPDATE player
SET unlocked_levels = 'L' || substr('00' || unlocked_level, -2)
WHERE unlocked_levels = '' OR unlocked_levels IS NULL;
