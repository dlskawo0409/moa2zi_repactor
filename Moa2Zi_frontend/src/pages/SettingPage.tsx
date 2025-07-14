import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { ArrowLeft } from "lucide-react";
import { toast, Toaster } from "sonner";
import { queryClient } from "@/lib/queryClient";
import { useAuthStore } from "@/stores/useAuthStore";
import { Switch } from "@/components/ui/switch";
import { useUserInfo } from "@/hooks/useUserInfo";
import { postLogout } from "@/services/auth";
import { updateMemberProfile } from "@/services/member";
import ProfleModal from "@components/signup/ProfileModal";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";

const SettingPage = () => {
  const navigate = useNavigate();
  const { data, refetch } = useUserInfo();
  const setIsLoggedIn = useAuthStore((state) => state.setIsLoggedIn);

  const [editing, setEditing] = useState<boolean>(false);
  const [nickname, setNickname] = useState<string>("");
  const [profileImage, setProfileImage] = useState<string>("MouseIcon");
  const [visibility, setVisibility] = useState<string>("ALL");
  const [alarmOn, setAlarmOn] = useState<boolean>(true);
  const [toastState, setToastState] = useState<boolean>(false);

  useEffect(() => {
    if (data) {
      setNickname(data.nickname);
      setProfileImage(data.profileImage);
      setVisibility(data.disclosure ?? "ALL");
      setAlarmOn(data.alarm ?? true);
    }
  }, [data]);

  const handleSubmit = async () => {
    if (!data) return;

    const nicknameChanged = nickname !== data.nickname;
    const profileImageChanged = profileImage !== data.profileImage;
    const disclosureChanged = visibility !== data.disclosure;
    const alarmChanged = alarmOn !== data.alarm;

    if (!nicknameChanged && !profileImageChanged && !disclosureChanged && !alarmChanged) {
      setToastState(false);
      toast("변경된 내용이 없습니다.");
      return;
    }

    const requestData = {
      nickname,
      birthday: data.birthday,
      gender: data.gender,
      profileImage,
      alarm: alarmOn,
      disclosure: visibility,
    };

    try {
      await updateMemberProfile(requestData);
      await refetch();
      setToastState(true);
      toast("프로필이 수정되었습니다!");
      setEditing(false);
    } catch (err) {
      setToastState(false);
      toast("수정 중 오류가 발생했습니다.");
    }
  };

  const handleCancel = () => {
    if (!data) return;
    setNickname(data.nickname);
    setProfileImage(data.profileImage);
    setVisibility(data.disclosure ?? "ALL");
    setAlarmOn(data.alarm ?? true);
    setEditing(false);
  };

  const handleLogout = async () => {
    try {
      const response = await postLogout();
      // console.log(response.data);
    } catch (error) {
      // console.log(error);
    }

    queryClient.removeQueries({ queryKey: ["accessToken"] });
    queryClient.removeQueries({ queryKey: ["userInfo"] });
    setIsLoggedIn(false);
    navigate("/login", { replace: true });
  };

  const sendMessageToAndroid = (state: boolean) => {
    if (window.AndroidInterface && window.AndroidInterface.showToast) {
      window.AndroidInterface.showToast(`React에서 보낸 메시지! ${state}`);
    }
  };

  const handleAlarm = () => {
    setAlarmOn((prev) => {
      const newState = !prev;
      // console.log(`click alarm ${newState}`);
      sendMessageToAndroid(newState);
      return newState;
    });
  };

  return (
    <>
      <div className="sticky top-0 flex items-center w-full h-[55px] px-5 bg-white border-b-[1px]">
        <div className="cursor-pointer" onClick={() => navigate(-1)}>
          <ArrowLeft />
        </div>
        <div className="absolute left-1/2 -translate-x-1/2 text-xl font-bold">프로필 및 설정</div>
      </div>
      {toastState ? (
        <Toaster position="top-center" toastOptions={{ className: "custom-toast-positive" }} />
      ) : (
        <Toaster position="top-center" toastOptions={{ className: "custom-toast-negative" }} />
      )}
      <div className="flex flex-col justify-center items-center w-full gap-4  px-4">
        <div className="w-full p-6 bg-white rounded-xl space-y-8">
          <div className="flex flex-col items-center gap-2">
            <label className="block text-md font-semibold text-gray-700">프로필 변경</label>{" "}
            <div className={editing ? "" : "pointer-events-none"}>
              <ProfleModal profileImage={profileImage} setProfileImage={setProfileImage} />
            </div>
            <input
              type="text"
              value={nickname}
              onChange={(e) => setNickname(e.target.value)}
              readOnly={!editing}
              className={`rounded px-3 py-2 text-center w-[50%] border transition
              ${
                editing
                  ? "border-gray-300 bg-white text-black focus:border-primary-500 focus:outline-none"
                  : "border-gray-200 bg-gray-100 text-gray-500 cursor-default"
              }
            `}
            />
          </div>

          <div className="space-y-6">
            <div className="flex justify-between items-center">
              <label className="block text-md font-semibold text-gray-700">가계부 공개 범위</label>
              {/* <div className="flex justify-center"> */}
              <Select value={visibility} onValueChange={setVisibility} disabled={!editing}>
                <SelectTrigger
                  className={`w-[50%] border rounded transition
                  ${
                    editing
                      ? "border-gray-300 bg-white text-black focus:border-primary-500 data-[state=open]:border-primary-500 focus:outline-none focus:ring-0 focus:ring-offset-0"
                      : "border-gray-200 bg-gray-100 text-gray-500 cursor-default"
                  }
                `}
                >
                  <SelectValue placeholder="공개 범위 선택" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="ALL" className="data-[state=checked]:text-primary-500">
                    전체 공개
                  </SelectItem>
                  <SelectItem value="FRIEND" className="data-[state=checked]:text-primary-500">
                    친구 공개
                  </SelectItem>
                  <SelectItem value="ONLY_ME" className="data-[state=checked]:text-primary-500">
                    비공개
                  </SelectItem>
                </SelectContent>
              </Select>
              {/* </div> */}
            </div>

            <div className="flex items-center justify-between">
              <span className="text-md font-semibold text-gray-700">알림 설정</span>
              <Switch checked={alarmOn} onCheckedChange={handleAlarm} disabled={!editing} />
            </div>
          </div>

          <div className="flex justify-center gap-3">
            {editing ? (
              <>
                <button
                  className="border px-4 py-2 rounded bg-white hover:bg-neutral-50"
                  onClick={handleCancel}
                >
                  취소
                </button>
                <button
                  className="px-4 py-2 rounded bg-primary-500 text-white hover:bg-primary-400"
                  onClick={handleSubmit}
                >
                  저장
                </button>
              </>
            ) : (
              <button
                className="px-4 py-2 rounded bg-primary-500 text-white hover:bg-primary-400"
                onClick={() => setEditing(true)}
              >
                수정하기
              </button>
            )}
          </div>
        </div>

        <div
          onClick={handleLogout}
          className="text-negative-500 font-medium cursor-pointer hover:underline"
        >
          로그아웃
        </div>
      </div>
    </>
  );
};

export default SettingPage;
