package potato;

public class DemonEntity extends EnemyEntity {
    public DemonEntity(double x, double y) {
        super(x, y, 0, 2.0, Math.PI/2, 100, 0.5, new Textures("/potato/sprites/entity/demon.png", 168, 168), 1.0);

        defineAnimation("idle", 1, 1, 100);
        defineAnimation("walking", 1, 3, 100);
        defineAnimation("attacking", 4, 2, 100);
        // Define stats
        setStat("attackRange", 1.5);
        setStat("attackDamage", 25.0);
        setStat("detectionRange", 10.0);
        setStat("attackCooldown", 1.0);
    }

    @Override
    public void update() {
        super.update();
        setState("walking");
    }
}