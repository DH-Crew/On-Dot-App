package com.dh.ondot.presentation.ui.theme

// Platform
const val ANDROID = "Android"
const val IOS = "IOS"
const val DATABASE_NAME = "ondot.db"

// 단어
const val WORD_NEXT = "다음"
const val WORD_HOUR = "시간"
const val WORD_MINUTE = "분"
const val WORD_MUTE = "무음"
const val WORD_AM = "오전"
const val WORD_PM = "오후"
const val WORD_REPEAT = "반복"
const val WORD_EVERYDAY = "매일"
const val WORD_WEEKDAY = "평일"
const val WORD_WEEKEND = "주말"
const val WORD_DATE = "날짜"
const val WORD_TIME = "시간"
const val WORD_SETTING = "세팅"
const val WORD_GENERAL = "일반"
const val WORD_HELP = "도움"
const val WORD_ACCOUNT = "계정"
const val WORD_WITHDRAW = "회원탈퇴"
const val WORD_LOGOUT = "로그아웃"
const val WORD_FINE = "괜찮아요"
const val WORD_CONFIRM = "확인"
const val WORD_COMPETE = "완료"
const val WORD_CANCEL = "취소"
const val WORD_SAVE = "저장"
const val WORD_DELETE = "삭제"
const val WORD_YES = "예"
const val WORD_NO = "아니요"
const val WORD_PREPARATION = "준비"
const val WORD_DEPARTURE = "출발"
const val WORD_DELETE_ACCOUNT_ACTION = "탈퇴하기"
const val WORD_RESTORE_ACTION = "되돌리기"
const val WORD_HOME = "집"
const val WORD_EVERYTIME = "에브리타임"

// 알람 카테고리
const val CATEGORY_GENERAL = "기본"
const val CATEGORY_BRIGHT_ENERGY = "밝은 에너지"
const val CATEGORY_FAST_INTENSE = "빠르고 강렬한"

// 버튼 텍스트
const val KAKAO_LOGIN_BUTTON_TEXT = "카카오로 계속하기"
const val APPLE_LOGIN_BUTTON_TEXT = "Apple로 로그인"
const val CREATE_SCHEDULE = "일정 생성"
const val PREPARATION_START_BUTTON_TEXT = "준비 시작하기"
const val SHOW_ROUTE_INFORMATION_BUTTON_TEXT = "경로안내 보기"

// ERROR
const val ERROR_GET_SCHEDULE_LIST = "일정 조회에 실패했습니다."
const val ERROR_SEARCH_PLACE = "장소 검색에 실패했습니다."
const val ERROR_GET_PLACE_HISTORY = "검색 기록 조회에 실패했습니다."
const val ERROR_GET_HOME_ADDRESS = "집 주소 조회에 실패했습니다."
const val ERROR_GET_SCHEDULE_ALARMS = "알람 정보 계산에 실패했습니다."
const val ERROR_CREATE_SCHEDULE = "일정 생성에 실패했습니다."
const val ERROR_GET_SCHEDULE_DETAIL = "일정 상세 조회에 실패했습니다."
const val ERROR_DELETE_SCHEDULE = "일정 삭제에 실패했습니다."
const val ERROR_EDIT_SCHEDULE = "일정 수정에 실패했습니다."
const val ERROR_LOGOUT = "로그아웃에 실패했습니다."
const val ERROR_WITHDRAW = "회원탈퇴에 실패했습니다."
const val ERROR_LOGIN = "로그인에 실패했습니다."
const val ERROR_SET_MAP_PROVIDER = "지도 공급자 저장에 실패했습니다."
const val ERROR_UPDATE_HOME_ADDRESS = "집 주소 변경에 실패했습니다."
const val ERROR_UPDATE_PREPARATION_TIME = "준비 시간 변경에 실패했습니다."
const val ERROR_GET_SCHEDULE_PREPARATION = "준비 정보 조회에 실패했습니다."
const val ERROR_INVALID_MINUTE_INPUT = "분은 59이하의 숫자로 입력해주세요."
const val ERROR_VALIDATE_EVERYTIME_TIMETABLE = "시간표를 가져오기에 실패했습니다."
const val ERROR_EMPTY_URL = "URL을 입력해주세요."

// SUCCESS
const val SUCCESS_DELETE_SCHEDULE = "일정이 삭제되었어요."
const val SUCCESS_EDIT_PREPARATION_TIME = "준비 시간이 수정되었어요."
const val SUCCESS_VALIDATE_TIMETABLE = "시간표 연동에 성공했어요."

// Onboarding
const val ONBOARDING1_TITLE = "평소 외출 준비하는데\n얼마나 소요되나요?"
const val ONBOARDING1_TITLE_HIGHLIGHT = "외출 준비"
const val ONBOARDING1_SUB_TITLE = "30분, 1시간 20분 등 자유롭게 적어주세요!"
const val ONBOARDING1_HOUR_PLACEHOLDER = "1자 이내의 숫자"
const val ONBOARDING1_MINUTE_PLACEHOLDER = "2자 이내의 숫자"

const val ONBOARDING2_TITLE = "빠른 일정 등록을 위한 주소를 입력해 주세요."
const val ONBOARDING2_TITLE_HIGHLIGHT = "주소"
const val ONBOARDING2_SUB_TITLE = "빠른 일정 등록에 꼭 필요한 정보예요. \n집 주소는 안전하게 저장되며 언제든 수정할 수 있어요."
const val ONBOARDING2_PLACEHOLDER = "도로명 주소"

const val ONBOARDING3_TITLE = "알람의 초기 사운드를 설정해 주세요."
const val ONBOARDING3_TITLE_HIGHLIGHT = "알람의 초기 사운드"
const val ONBOARDING3_SUB_TITLE = "추후에 마이페이지에서 수정할 수 있어요."

const val ONBOARDING4_TITLE = "ONDOT을 사용하면서\n어떤 것을 가장 기대하나요?"
const val ONBOARDING4_TITLE_HIGHLIGHT = "기대"
const val ONBOARDING4_SUB_TITLE = "한 개의 항목만 선택이 가능해요."
const val ONBOARDING4_ANSWER1 = "지각 방지"
const val ONBOARDING4_ANSWER2 = "신경 쓰임 해소"
const val ONBOARDING4_ANSWER3 = "간편한 일정 관리"
const val ONBOARDING4_ANSWER4 = "정확한 출발 타이밍 알림"

const val ONBOARDING5_TITLE = "해당 목표를 달성하고 싶은 \n이유는 무엇인가요?"
const val ONBOARDING5_TITLE_HIGHLIGHT = "이유"
const val ONBOARDING5_SUB_TITLE = "한 개의 항목만 선택이 가능해요."
const val ONBOARDING5_ANSWER1 = "여유 있는 하루를 보내고 싶어서"
const val ONBOARDING5_ANSWER2 = "중요한 사람과의 약속을 잘 지키고 싶어서"
const val ONBOARDING5_ANSWER3 = "계획한 하루를 흐트러짐 없이 보내고 싶어서"
const val ONBOARDING5_ANSWER4 = "지각 걱정 없이 신뢰받는 사람이 되고 싶어서"

// Home
const val EMPTY_SCHEDULE = "지금 바로 알람을 생성해보세요!"
const val QUICK_ADD = "빠른 일정 생성"
const val GENERAL_ADD = "일반 일정 생성"
const val ADD_SCHEDULE = "알람 추가"
const val EMPTY_PREPARATION_ALARM = "준비시작 알람 없음"
const val CREATE_SCHEDULE_GUIDE = "예정된 일정이 있나요?"
const val ALARM_IMMINENT = "곧 알람이 울려요"

fun appointmentTime(time: String) = "일정 $time"

// General
const val SCHEDULE_REPEAT_TITLE = "약속시간과 날짜를 알려주세요."
const val PLACE_PICKER_TITLE = "출발지와 약속 장소를 알려주세요."
const val DEPARTURE_INPUT_PLACEHOLDER = "출발지: "
const val ARRIVAL_INPUT_PLACEHOLDER = "도착지: "
const val DEPARTURE_FROM_HOME = "집에서 출발해요"
const val ROUTE_CALCULATE_LABEL = "약속 장소에 촉박하게 도착하지 않도록\n10분~15분 여유시간을 확보해 드릴게요"
const val NEW_SCHEDULE_LABEL = "새로운 일정"
const val DEPARTURE_ALARM_LABEL = "출발 알람"
const val PREPARATION_ALARM_LABEL = "준비 알람"

// General/BottomSheet
const val GENERAL_SCHEDULE_BOTTOM_SHEET_TITLE = "출발 전 챙겨야할 게 있나요?"
const val GENERAL_SCHEDULE_BOTTOM_SHEET_MEDICINE = "건강에 중요한 약을 챙겨야해요"
const val GENERAL_SCHEDULE_BOTTOM_SHEET_MATERIAL = "준비물을 적어두면 출발알람이 울릴 때\n알려드려요!"

// Setting
const val SETTING_HOME_ADDRESS = "집 주소 설정"
const val SETTING_NAV_MAP = "길 안내 지도 설정"
const val SETTING_ALARM_DEFAULT = "알람 초기값 설정"
const val SETTING_PREPARE_TIME = "준비 시간 설정"
const val SETTING_FEEDBACK = "피드백 보내기"
const val SETTING_SERVICE_POLICY = "서비스 정책"
const val SETTING_HOME_ADDRESS_EDIT_TITLE = "주소를 입력해 주세요."
const val SETTING_NAV_MAP_PROVIDER_TITLE = "자주 사용하는\n지도를 설정해주세요"
const val SETTING_NAV_MAP_PROVIDER_GUIDE = "출발 알람 이후 길 안내에 사용될 예정이에요."

// Alarm
fun alarmRingTitle(time: String) = "출발하기까지 $time\n어서 준비를 시작하세요!"

fun snoozeIntervalLabel(snoozeInterval: Int) = "${snoozeInterval}분 알람 미루기"

fun formatRemainingSnoozeTime(
    minute: Int,
    second: Int,
): String {
    val minuteString = minute.toString().padStart(2, '0')
    val secondString = second.toString().padStart(2, '0')
    return "$minuteString:$secondString"
}

const val DEPARTURE_ALARM_RING_TITLE = "지금 출발해야\n일정에 늦지 않을 수 있어요!"

fun departureSnoozedTitle(time: String) = "일정까지 $time 전\n어서 출발하세요!"

const val YESTERDAY_LABEL = "약속 전 날"
const val SCHEDULE_MEDICINE = "출발 전, 약을 꼭 챙기세요!"

fun schedulePreparation(content: String) = "출발 전, $content 꼭 챙기세요!"

// Dialog
const val DELETE_ALARM_TITLE = "알람 삭제"
const val DELETE_ALARM_CONTENT = "정말 알람을 삭제하시겠어요?"
const val MAP_PROVIDER_TITLE = "어떤 지도 앱을\n자주 사용하시나요?"
const val MAP_PROVIDER_CONTENT = "길 안내에 사용될 예정이에요."

// Setting
const val LOGOUT_SUCCESS_MESSAGE = "정상적으로 로그아웃되었습니다."
const val WITHDRAW_SUCCESS_MESSAGE = "정상적으로 탈퇴되었습니다."
const val DELETION_ANSWER1 = "지각 방지에 효과를 못 느꼈어요."
const val DELETION_ANSWER2 = "일정 등록이나 사용이 번거로웠어요."
const val DELETION_ANSWER3 = "알림이 너무 많거나 타이밍이 맞지 않았어요."
const val DELETION_ANSWER4 = "제 생활에 딱히 쓸 일이 없었어요."
const val LOGOUT_DIALOG_CONTENT = "정말 로그아웃 하시겠어요?"

// DeleteAccount
const val DELETE_ACCOUNT_CAUTION_TITLE = "회원탈퇴를 하기 전\n안내 사항을 확인해주세요."
const val DELETE_ACCOUNT_GUIDE1 = "회원탈퇴를 진행한 뒤, 다시 온닷을 가입해도\n이전 계정 데이터는 복원되지 않아요."
const val DELETE_ACCOUNT_GUIDE2 = "탈퇴 시 회원님의 개인정보는 개인정보처리방침에\n따라 탈퇴일로부터 30일간 보관 후 삭제돼요."
const val DELETE_ACCOUNT_GUIDE3 = "탈퇴 사유를 선택해주세요."
const val DELETE_ACCOUNT_GUIDE_BOLD1 = "다시 온닷을 가입해도"
const val DELETE_ACCOUNT_GUIDE_BOLD2 = "이전 계정 데이터는 복원되지 않아요."
const val DELETE_ACCOUNT_GUIDE_BOLD3 = "탈퇴일로부터 30일간 보관 후 삭제돼요."
const val DELETE_ACCOUNT_DIALOG_TITLE = "회원탈퇴 완료"
const val DELETE_ACCOUNT_DIALOG_CONTENT = "그동안 온닷을 이용해주셔서\n감사합니다. 더 좋은 서비스를 \n제공하기 위해 노력하겠습니다."

// ServiceTerms
const val SERVICE_TERMS_TITLE = "서비스 정책"
const val SERVICE_NOTIFICATION_TITLE = "공지사항"

// LocalNotification
const val NOTIFICATION_TITLE = "준비물을 잊지 말고 챙기세요!"

// LandingScreen
const val LANDING_SCREEN_TITLE = "에브리타임 시간표 연동"
const val START_INTEGRATION = "연동 시작하기"
const val HERO_SECTION_TITLE1 = "에브리타임 연동,"
const val HERO_SECTION_TITLE2 = "왜 필요할까요?"
const val HERO_SECTION_CONTENT1 = "통학하는 대학생에게 시간표는 곧 생활 패턴이에요\n그런데 매 학기, 매주 수업 시간마다\n알람을 직접 설정하는 건 너무 번거롭죠"
const val HERO_SECTION_CONTENT2 = "에브리타임 시간표를 연동하면\n수업 시간에 맞춰 알람이 자동으로 생성돼요"
const val HERO_SECTION_CONTENT3 = "더 이상 매주 반복 설정하지 않아도 돼요"
const val AUTO_ALARM_SECTION_TITLE1 = "시간표대로 알람 생성,"
const val AUTO_ALARM_SECTION_TITLE2 = "이제 직접 맞추지 마세요"
const val AUTO_ALARM_SECTION_CONTENT1 = "월요일 9시 수업\n매주 일요일 밤마다 알람 다시 맞추고 있지 않나요?\n준비 시간 계산하고, 출발 시간 계산하고,\n지도 다시 켜보고.."
const val AUTO_ALARM_SECTION_CONTENT2 = "이제 그 과정, 온닷이 대신할게요"
const val BENEFITS_SECTION_TITLE1 = "연동하면 이런 점이 좋아요"
const val BENEFITS_SECTION_TITLE1_HIGHLIGHT = "이런 점이 좋아요"
const val BENEFITS_SECTION_CONTENT1 = "수업 시간에 맞춰 자동 알람 생성"
const val BENEFITS_SECTION_DESCRIPTION1 = "시간표 기반으로 준비 알람 + 출발 알람이 자동으로 만들어져요"
const val BENEFITS_SECTION_CONTENT2 = "내 준비 시간까지 고려"
const val BENEFITS_SECTION_DESCRIPTION2 = "내 준비 소요 시간 + 이동 시간을 반영해 몇 시부터 준비해야 하는지 알려줘요"
const val BENEFITS_SECTION_CONTENT3 = "지도 연결도 한 번에"
const val BENEFITS_SECTION_DESCRIPTION3 = "출발 알람 후 바로 지도 앱으로 연결되어 헤매지 않고 이동 가능해요"
const val BENEFITS_SECTION_CONTENT4 = "매주 반복 설정 끝"
const val BENEFITS_SECTION_DESCRIPTION4 = "한 번 연동하면 학기 내내 자동 관리 가능해요"
const val HOW_TO_CONNECT_SECTION_TITLE1 = "지금 바로"
const val HOW_TO_CONNECT_SECTION_TITLE2 = "간편하게 연동하세요!"
const val HOW_TO_CONNECT_SECTION_TITLE2_HIGHLIGHT = "간편하게 연동"
const val HOW_TO_CONNECT_SECTION_CONTENT1 = "에브리타임에서 시간표를 ‘전체 공개’로 변경"
const val HOW_TO_CONNECT_SECTION_DESCRIPTION1 = "설정 아이콘 → 공개 범위 변경 → 전체 공개"
const val HOW_TO_CONNECT_SECTION_CONTENT2 = "‘URL 복사' 클릭"
const val HOW_TO_CONNECT_SECTION_DESCRIPTION2 = "설정 아이콘 → URL 복사"
const val HOW_TO_CONNECT_SECTION_CONTENT3 = "복사한 URL 온닷에 붙여넣기"
const val HOW_TO_CONNECT_SECTION_DESCRIPTION3 = "꾸욱-눌러 붙여넣어 주세요"
const val URL_INPUT_TITLE = "복사한 URL링크를 붙여주세요."
const val URL_INPUT_HIGHLIGHT = "복사한 URL링크"
const val URL_INPUT_GUIDE = "시간표의 공개 범위를 잠시 ‘전체 공개'로 변경해 주세요.\n(설정 아이콘 → 공개 범위 변경 → 전체 공개)"
const val PASTE_URL = "링크 붙여넣기"
