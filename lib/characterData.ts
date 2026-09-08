/**
 * CHARACTER DATA & OPTIONS (Prompt 02 of 20: CHARACTER SYSTEM)
 *
 * Curated lists of countries, cities, appearance styles, colors, talents,
 * and name generators for character creation on both Web and Android.
 */

export const COUNTRIES_AND_CITIES: Record<string, string[]> = {
  'United States': ['New York', 'Los Angeles', 'Chicago', 'Houston', 'Miami', 'San Francisco'],
  'United Kingdom': ['London', 'Manchester', 'Birmingham', 'Edinburgh', 'Glasgow'],
  'Japan': ['Tokyo', 'Kyoto', 'Osaka', 'Yokohama', 'Sapporo'],
  'Germany': ['Berlin', 'Munich', 'Frankfurt', 'Hamburg', 'Cologne'],
  'Canada': ['Toronto', 'Vancouver', 'Montreal', 'Calgary', 'Ottawa'],
  'Australia': ['Sydney', 'Melbourne', 'Brisbane', 'Perth', 'Adelaide'],
  'France': ['Paris', 'Lyon', 'Marseille', 'Nice', 'Bordeaux'],
  'Brazil': ['São Paulo', 'Rio de Janeiro', 'Salvador', 'Brasília'],
  'South Korea': ['Seoul', 'Busan', 'Incheon', 'Daegu'],
  'Italy': ['Rome', 'Milan', 'Florence', 'Naples', 'Venice'],
};

export const COUNTRIES = Object.keys(COUNTRIES_AND_CITIES);

export const GENDERS = ['Male', 'Female', 'Non-Binary'] as const;

export const SEXUALITIES = [
  'Heterosexual',
  'Homosexual',
  'Bisexual',
  'Asexual',
] as const;

export const TALENTS = [
  { id: 'None', label: 'None', desc: 'Generalist — Balanced, no innate specialization' },
  { id: 'Acting', label: 'Acting', desc: 'Dramatic Arts — Charismatic presence and emotional projection' },
  { id: 'Crime', label: 'Crime', desc: 'Underworld Cunning — Stealth, threat detection, and street instinct' },
  { id: 'Dealing', label: 'Dealing', desc: 'Commerce — High-stakes negotiation and trade acumen' },
  { id: 'Modeling', label: 'Modeling', desc: 'High Aesthetics — Photogenic magnetism and poise' },
  { id: 'Music', label: 'Music', desc: 'Acoustic Genius — Rhythmic intuition and auditory composition' },
  { id: 'Sports', label: 'Sports', desc: 'Kinetic Athletics — Explosive physical coordination and stamina' },
] as const;

export const SKIN_TONES = [
  { name: 'Fair', hex: '#FDE2D2' },
  { name: 'Light', hex: '#F4CFB7' },
  { name: 'Medium', hex: '#E0AC84' },
  { name: 'Olive', hex: '#C69165' },
  { name: 'Tan', hex: '#A87046' },
  { name: 'Dark', hex: '#7D4E2D' },
  { name: 'Deep', hex: '#4A2C1A' },
];

export const EYE_COLORS = [
  { name: 'Brown', hex: '#5A3825' },
  { name: 'Blue', hex: '#3A75C4' },
  { name: 'Green', hex: '#388E3C' },
  { name: 'Hazel', hex: '#8D6E63' },
  { name: 'Amber', hex: '#FFB300' },
  { name: 'Gray', hex: '#78909C' },
];

export const EYE_STYLES = ['Almond', 'Round', 'Hooded', 'Deep-Set', 'Monolid'];

export const BROW_STYLES = ['Straight', 'Arched', 'Soft Arch', 'Thick Bushy', 'Thin Curved'];

export const HAIR_COLORS = [
  { name: 'Black', hex: '#1E1E1E' },
  { name: 'Dark Brown', hex: '#3E2723' },
  { name: 'Chestnut', hex: '#5D4037' },
  { name: 'Golden Blonde', hex: '#E0B050' },
  { name: 'Auburn Red', hex: '#B71C1C' },
  { name: 'Platinum', hex: '#E0E0E0' },
  { name: 'Silver Gray', hex: '#9E9E9E' },
];

export const HAIR_STYLES = [
  'Short Crop',
  'Buzz Cut',
  'Side Part',
  'Medium Waves',
  'Long Flow',
  'Afro',
  'Slicked Back',
  'Bald',
];

export const FACIAL_HAIR_STYLES = [
  'Clean Shaven',
  'Light Stubble',
  'Full Beard',
  'Goatee',
  'Classic Mustache',
];

export const FIRST_NAMES = {
  Male: ['Julian', 'Marcus', 'Adrian', 'Leo', 'Ethan', 'Lucas', 'Oliver', 'Dante', 'Kai', 'Liam'],
  Female: ['Elena', 'Sophia', 'Maya', 'Clara', 'Aria', 'Chloe', 'Isla', 'Valerie', 'Zoe', 'Nora'],
  'Non-Binary': ['Rowan', 'Alex', 'Jordan', 'Morgan', 'Taylor', 'Sam', 'Casey', 'Avery', 'Riley', 'Cameron'],
};

export const LAST_NAMES = [
  'Vance',
  'Sterling',
  'Mercer',
  'Hawthorne',
  'Sinclair',
  'Cross',
  'Blackwood',
  'Valenti',
  'Kovacs',
  'Chen',
  'Saito',
  'Schmidt',
  'Moreau',
  'Silva',
];

export function getRandomName(gender: string): { firstName: string; lastName: string } {
  const gKey = (gender === 'Female' || gender === 'Non-Binary') ? gender : 'Male';
  const fList = FIRST_NAMES[gKey];
  const firstName = fList[Math.floor(Math.random() * fList.length)];
  const lastName = LAST_NAMES[Math.floor(Math.random() * LAST_NAMES.length)];
  return { firstName, lastName };
}
