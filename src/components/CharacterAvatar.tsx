'use client';

import React from 'react';
import { SKIN_TONES, EYE_COLORS, HAIR_COLORS } from '@/lib/characterData';

interface CharacterAvatarProps {
  skinTone: string;
  eyeStyle: string;
  eyeColor: string;
  browStyle: string;
  hairStyle: string;
  hairColor: string;
  facialHairStyle: string;
  facialHairColor: string;
  size?: number;
  className?: string;
}

export const CharacterAvatar: React.FC<CharacterAvatarProps> = ({
  skinTone,
  eyeStyle,
  eyeColor,
  browStyle,
  hairStyle,
  hairColor,
  facialHairStyle,
  facialHairColor,
  size = 160,
  className = '',
}) => {
  const skinHex = SKIN_TONES.find((s) => s.name === skinTone)?.hex || '#E0AC84';
  const eyeHex = EYE_COLORS.find((e) => e.name === eyeColor)?.hex || '#5A3825';
  const hairHex = HAIR_COLORS.find((h) => h.name === hairColor)?.hex || '#1E1E1E';
  const facialHairHex =
    HAIR_COLORS.find((h) => h.name === facialHairColor)?.hex || hairHex;

  // Eyebrow path depending on style
  const renderBrows = () => {
    switch (browStyle) {
      case 'Arched':
        return (
          <>
            <path
              d="M 33 51 Q 42 45 51 50"
              stroke={hairHex}
              strokeWidth="3.2"
              strokeLinecap="round"
              fill="none"
            />
            <path
              d="M 69 50 Q 78 45 87 51"
              stroke={hairHex}
              strokeWidth="3.2"
              strokeLinecap="round"
              fill="none"
            />
          </>
        );
      case 'Thick Bushy':
        return (
          <>
            <path
              d="M 32 50 Q 42 48 52 50"
              stroke={hairHex}
              strokeWidth="5"
              strokeLinecap="round"
              fill="none"
            />
            <path
              d="M 68 50 Q 78 48 88 50"
              stroke={hairHex}
              strokeWidth="5"
              strokeLinecap="round"
              fill="none"
            />
          </>
        );
      case 'Thin Curved':
        return (
          <>
            <path
              d="M 34 51 Q 42 47 50 51"
              stroke={hairHex}
              strokeWidth="1.8"
              strokeLinecap="round"
              fill="none"
            />
            <path
              d="M 70 51 Q 78 47 86 51"
              stroke={hairHex}
              strokeWidth="1.8"
              strokeLinecap="round"
              fill="none"
            />
          </>
        );
      case 'Soft Arch':
        return (
          <>
            <path
              d="M 33 51 Q 42 47 51 51"
              stroke={hairHex}
              strokeWidth="3"
              strokeLinecap="round"
              fill="none"
            />
            <path
              d="M 69 51 Q 78 47 87 51"
              stroke={hairHex}
              strokeWidth="3"
              strokeLinecap="round"
              fill="none"
            />
          </>
        );
      case 'Straight':
      default:
        return (
          <>
            <path
              d="M 33 50 L 51 50"
              stroke={hairHex}
              strokeWidth="3.5"
              strokeLinecap="round"
              fill="none"
            />
            <path
              d="M 69 50 L 87 50"
              stroke={hairHex}
              strokeWidth="3.5"
              strokeLinecap="round"
              fill="none"
            />
          </>
        );
    }
  };

  // Eyes rendering depending on style & color
  const renderEyes = () => {
    let ry = 4.5;
    let rx = 7;
    if (eyeStyle === 'Round') {
      rx = 6.5;
      ry = 6;
    } else if (eyeStyle === 'Hooded') {
      rx = 7;
      ry = 3.5;
    } else if (eyeStyle === 'Deep-Set') {
      rx = 6;
      ry = 4;
    } else if (eyeStyle === 'Monolid') {
      rx = 7.5;
      ry = 3.2;
    }

    return (
      <>
        {/* Left eye sclera */}
        <ellipse cx="42" cy="58" rx={rx} ry={ry} fill="#FFFFFF" />
        {/* Left iris */}
        <circle cx="42" cy="58" r="3.2" fill={eyeHex} />
        {/* Left pupil */}
        <circle cx="42" cy="58" r="1.6" fill="#111111" />
        {/* Left highlight */}
        <circle cx="43" cy="57" r="0.8" fill="#FFFFFF" />

        {/* Right eye sclera */}
        <ellipse cx="78" cy="58" rx={rx} ry={ry} fill="#FFFFFF" />
        {/* Right iris */}
        <circle cx="78" cy="58" r="3.2" fill={eyeHex} />
        {/* Right pupil */}
        <circle cx="78" cy="58" r="1.6" fill="#111111" />
        {/* Right highlight */}
        <circle cx="79" cy="57" r="0.8" fill="#FFFFFF" />

        {/* Upper eyelid contour */}
        <path
          d={`M ${42 - rx} 58 Q 42 ${58 - ry} ${42 + rx} 58`}
          stroke="#333333"
          strokeWidth="1.2"
          fill="none"
        />
        <path
          d={`M ${78 - rx} 58 Q 78 ${58 - ry} ${78 + rx} 58`}
          stroke="#333333"
          strokeWidth="1.2"
          fill="none"
        />
      </>
    );
  };

  // Facial Hair rendering
  const renderFacialHair = () => {
    switch (facialHairStyle) {
      case 'Light Stubble':
        return (
          <path
            d="M 44 76 Q 60 84 76 76 Q 68 90 60 90 Q 52 90 44 76 Z"
            fill={facialHairHex}
            opacity="0.35"
          />
        );
      case 'Full Beard':
        return (
          <path
            d="M 33 66 C 33 88, 42 98, 60 98 C 78 98, 87 88, 87 66 C 82 72, 74 76, 68 76 C 60 76, 52 76, 44 76 C 39 74, 35 70, 33 66 Z"
            fill={facialHairHex}
          />
        );
      case 'Goatee':
        return (
          <path
            d="M 50 74 Q 60 72 70 74 Q 67 92 60 94 Q 53 92 50 74 Z"
            fill={facialHairHex}
          />
        );
      case 'Classic Mustache':
        return (
          <path
            d="M 46 72 Q 60 74 74 72 Q 60 77 46 72 Z"
            fill={facialHairHex}
          />
        );
      case 'Clean Shaven':
      default:
        return null;
    }
  };

  // Hair style rendering
  const renderHair = () => {
    switch (hairStyle) {
      case 'Buzz Cut':
        return (
          <path
            d="M 30 50 C 30 26, 90 26, 90 50 C 90 35, 30 35, 30 50 Z"
            fill={hairHex}
            opacity="0.9"
          />
        );
      case 'Side Part':
        return (
          <path
            d="M 28 50 C 28 22, 60 18, 92 30 C 88 20, 48 20, 28 42 Z"
            fill={hairHex}
          />
        );
      case 'Medium Waves':
        return (
          <path
            d="M 26 56 C 26 24, 94 24, 94 56 C 96 66, 90 74, 88 74 C 92 48, 86 28, 60 28 C 34 28, 28 48, 32 74 C 30 74, 24 66, 26 56 Z"
            fill={hairHex}
          />
        );
      case 'Long Flow':
        return (
          <path
            d="M 24 60 C 24 20, 96 20, 96 60 C 98 84, 88 95, 86 96 C 93 68, 88 30, 60 30 C 32 30, 27 68, 34 96 C 32 95, 22 84, 24 60 Z"
            fill={hairHex}
          />
        );
      case 'Afro':
        return (
          <circle cx="60" cy="46" r="38" fill={hairHex} />
        );
      case 'Slicked Back':
        return (
          <path
            d="M 28 48 C 30 22, 90 22, 92 48 C 88 28, 32 28, 28 48 Z"
            fill={hairHex}
          />
        );
      case 'Bald':
        return null;
      case 'Short Crop':
      default:
        return (
          <path
            d="M 28 48 C 28 24, 92 24, 92 48 C 86 32, 60 30, 28 48 Z"
            fill={hairHex}
          />
        );
    }
  };

  return (
    <div
      className={`relative flex items-center justify-center rounded-2xl bg-gradient-to-b from-slate-900 via-slate-800 to-slate-950 p-3 shadow-inner border border-slate-700/60 ${className}`}
      style={{ width: size, height: size }}
    >
      <svg
        viewBox="0 0 120 120"
        className="w-full h-full overflow-visible drop-shadow-md"
      >
        {/* Background Aura */}
        <circle cx="60" cy="60" r="54" fill="#182234" opacity="0.4" />

        {/* Neck */}
        <path
          d="M 48 76 L 48 102 C 54 105, 66 105, 72 102 L 72 76 Z"
          fill={skinHex}
        />

        {/* Hair Underlayer (for long styles) */}
        {(hairStyle === 'Long Flow' || hairStyle === 'Afro') && renderHair()}

        {/* Head Base */}
        <path
          d="M 32 50 C 32 30, 88 30, 88 50 C 88 74, 76 90, 60 90 C 44 90, 32 74, 32 50 Z"
          fill={skinHex}
        />

        {/* Ears */}
        <ellipse cx="30" cy="58" rx="4" ry="7" fill={skinHex} />
        <ellipse cx="90" cy="58" rx="4" ry="7" fill={skinHex} />

        {/* Eyebrows */}
        {renderBrows()}

        {/* Eyes */}
        {renderEyes()}

        {/* Nose */}
        <path
          d="M 60 58 L 57 68 L 63 68"
          stroke="#A87046"
          strokeWidth="1.2"
          strokeLinecap="round"
          strokeLinejoin="round"
          fill="none"
          opacity="0.6"
        />

        {/* Mouth */}
        <path
          d="M 52 76 Q 60 80 68 76"
          stroke="#8C4E3D"
          strokeWidth="1.8"
          strokeLinecap="round"
          fill="none"
        />

        {/* Facial Hair */}
        {renderFacialHair()}

        {/* Hair Top Layer */}
        {hairStyle !== 'Long Flow' && hairStyle !== 'Afro' && renderHair()}
      </svg>
    </div>
  );
};
