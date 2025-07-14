import { motion } from "framer-motion";

const particles = Array.from({ length: 18 });

const colors = ["#FFAA64", "#FFD2A1", "#FFE5C1", "#ffffff"];

const ExplosionParticles = ({ className }: SVGProps) => {
  return (
    <div className="absolute inset-0 pointer-events-none overflow-hidden z-10">
      {particles.map((_, i) => {
        const angle = (i / particles.length) * 2 * Math.PI;
        const x = Math.cos(angle) * 150;
        const y = Math.sin(angle) * 150;
        const color = colors[i % colors.length];

        return (
          <motion.div
            key={i}
            initial={{ x: 0, y: 0, opacity: 1, scale: 1 }}
            animate={{
              x,
              y,
              opacity: 0,
              scale: 1.5,
              transition: {
                duration: 1.2,
                ease: "easeOut",
              },
            }}
            className="w-3 h-3 rounded-full absolute left-1/2 top-1/2"
            style={{
              backgroundColor: color,
              transform: "translate(-50%, -50%)",
            }}
          />
        );
      })}
    </div>
  );
};

export default ExplosionParticles;
