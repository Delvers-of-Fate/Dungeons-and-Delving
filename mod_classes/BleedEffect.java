package mod_classes;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import com.interrupt.dungeoneer.Audio;
import com.interrupt.dungeoneer.entities.Actor;
import com.interrupt.dungeoneer.entities.Particle;
import com.interrupt.dungeoneer.entities.Player;
import com.interrupt.dungeoneer.entities.items.Weapon.DamageType;
import com.interrupt.dungeoneer.game.CachePools;
import com.interrupt.dungeoneer.entities.items.Weapon;
import com.interrupt.dungeoneer.statuseffects.StatusEffect;
import com.interrupt.dungeoneer.game.Game;
import com.interrupt.dungeoneer.statuseffects.PoisonEffect;
import com.interrupt.dungeoneer.game.Level;
import com.interrupt.helpers.PlayerHistory;
import com.interrupt.managers.StringManager;
import java.util.Random;

public class BleedEffect
  extends PoisonEffect
{
  public Particle effectParticle;
  public Vector3 effectOffset = new Vector3(0.0F, 0.0F, 0.55F);
  public float damageTimer = 60.0F;
  public int damage = 1;
  private float dtimer = 0.0F;
  private boolean canKill = false;
  private float particleInterval = 20.0F;
  private float particleTimer = 0.0F;
  
  public BleedEffect()
  {
    this(1000, 160, 1, false);
  }
  
  public BleedEffect(int time, int damageTimer, int damage, boolean canKill)
  {
    this.name = StringManager.get("statuseffects.BleedEffect.defaultNameText");
    this.timer = time;
    this.statusEffectType = StatusEffect.StatusEffectType.POISON;
    this.damageTimer = damageTimer;
    this.damage = damage;
    this.canKill = canKill;
  }
  
  public void doTick(Actor owner, float delta)
  {
    this.dtimer += delta;
    this.particleTimer += delta;
    if (this.particleTimer > this.particleInterval)
    {
      this.particleTimer = 0.0F;
      createPoisonParticle(owner, Game.rand.nextFloat() * 0.25F + 0.25F);
    }
    if (this.dtimer > this.damageTimer)
    {
      this.dtimer = 0.0F;
      if ((owner.hp - this.damage <= 0) && (!this.canKill)) {
        return;
      }
      owner.takeDamage(this.damage, Weapon.DamageType.PHYSICAL, null);
      doBleedEffect(owner);
      
      Audio.playPositionedSound("mg_pass_poison.mp3", new Vector3(owner.x, owner.y, owner.z), 0.5F, 6.0F);
    }
  }
  
  public void onStatusBegin(Actor owner)
  {
    doBleedEffect(owner);
  }
  
  private void doBleedEffect(Actor owner)
  {
    int impactParticleCount = Game.rand.nextInt(8) + 2;
    Vector3 cameraRight = Game.camera.direction.crs(new Vector3(0.0F, 1.0F, 0.0F)).nor();
    Vector3 particleDirection = CachePools.getVector3();
    for (int i = 0; i < impactParticleCount; i++) {
      createPoisonParticle(owner, 0.5F);
    }
    CachePools.freeVector3(particleDirection);
  }
  
  private void createPoisonParticle(Actor owner, float scale)
  {
    if (!this.showParticleEffect) {
      return;
    }
    float positionOffset = 0.0F;
    float spreadMod = 1.0F;
    float zMod = 0.0F;
    if ((owner instanceof Player))
    {
      positionOffset = 0.5F;
      spreadMod = 2.75F;
      zMod = -0.3F;
    }
    Particle p = CachePools.getParticle();
    p.tex = (80 + Game.rand.nextInt(3));
    p.lifetime = 90.0F;
    p.scale = scale;
    p.startScale = 1.0F;
    p.endScale = 0.125F;
    p.fullbrite = true;
    p.checkCollision = false;
    p.floating = true;
    p.x = (owner.x + (Game.rand.nextFloat() * scale - scale * 0.5F) * spreadMod + positionOffset);
    p.y = (owner.y + (Game.rand.nextFloat() * scale - scale * 0.5F) * spreadMod + positionOffset);
    p.z = (owner.z + zMod);
    
    p.za = (Game.rand.nextFloat() * 0.004F + 0.004F);
    
    Game.GetLevel().SpawnNonCollidingEntity(p);
  }
  
  public void forPlayer(Player player)
  {
    player.history.poisoned();
  }
  
  public void onStatusEnd(Actor owner)
  {
    this.active = false;
  }
}
