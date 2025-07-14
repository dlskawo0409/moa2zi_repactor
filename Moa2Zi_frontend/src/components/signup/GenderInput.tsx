interface GenderInputProps {
  gender: string;
  setGender: (value: string) => void;
}

const GenderInput = ({ gender, setGender }: GenderInputProps) => {
  return (
    <div className="flex flex-col w-full gap-1">
      <div className="px-2 text-primary-500 font-semibold">성별</div>
      <div className="flex gap-32 justify-center">
        <label className="flex items-center">
          <input
            type="radio"
            name="gender"
            value="MALE"
            checked={gender === "MALE"}
            onChange={() => setGender("MALE")}
            className="mr-2"
          />
          남성
        </label>
        <label className="flex items-center">
          <input
            type="radio"
            name="gender"
            value="FEMALE"
            checked={gender === "FEMALE"}
            onChange={() => setGender("FEMALE")}
            className="mr-2"
          />
          여성
        </label>
      </div>
    </div>
  );
};

export default GenderInput;
