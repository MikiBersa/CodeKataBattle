export enum AccountType {
  None = '',
  Educator = 'EDUCATOR',
  Student = 'STUDENT'
}

export enum EvalParameter {
  TESTS = "TESTS",
  TIMELINESS = "TIMELINESS",
  MANUAL = "MANUAL"
}

export enum StaticParameter {
  QUALITY = "QUALITY",
  SECURITY = "SECURITY",
  RELIABILITY = "RELIABILITY"
}

export enum NotificationType {
  NEW_TOURNAMENT = "NEW_TOURNAMENT",
  NEW_BATTLE = "NEW_BATTLE",
  NEW_INVITE = "NEW_INVITE", 
  INVITE_STATUS_UPDATE = "INVITE_STATUS_UPDATE",
  NEW_REPOSITORY_INVITE = "NEW_REPOSITORY_INVITE",
  GROUP_REMOVED_FROM_BATTLE = "GROUP_REMOVED_FROM_BATTLE",
  NEW_RANK_AVAILABLE = "NEW_RANK_AVAILABLE",
  SUCCESSFUL_BATTLE_ENROLLMENT = "SUCCESSFUL_BATTLE_ENROLLMENT",
  SUCCESSFUL_TOURNAMENT_SUBSCRIPTION = "SUCCESSFUL_TOURNAMENT_SUBSCRIPTION",
  MANUAL_EVALUATION_REQUIRED = "MANUAL_EVALUATION_REQUIRED"
}

export enum TestStatus {
  FAILED = "FAILED",
  PASSED = "PASSED"
}

export type NotificationContext = {
  display: string
  context_url: (tournament_title: string, battle_title: string) => string
}

export type LoginRequest = {
  email_or_username: string
  password: string
}

export type RegisterRequest = {
  username: string
  email: string
  password: string
  account_type: string
}

export type Battle = {
  id: string
  title: string
  min_group_size: number
  max_group_size: number
  description: string
  repository: string
  enrollment_deadline: string
  submission_deadline: string
  manual_evaluation: boolean
  groups: Group[]
  evaluation_parameters: string[]
}

export type BattleCreationRequest = {
  tournament_title: string
  battle_title: string
  min_group_size: number
  max_group_size: number
  description: string
  enrollment_deadline: string
  submission_deadline: string
  manual_evaluation: boolean
  evaluation_parameters: string[]
  tests_file_name: string
  project_language: string
}

export type BattleEnrollmentRequest = {
  tournament_title: string
  battle_title: string
  invited_members: string[]
}

export type BattleInfoResponse = {
  battle: Battle
  leaderboard: { username: string; score: number }[]
}

export type CardInfoEducator = {
  tournament_title: string
  tournament_owner: string
  subscribed_students_count: number
  number_of_battles: number
  subscription_deadline: string
  is_open: boolean
  educators: Educator[]
}

export type CardInfoStudent = {
  tournament_title: string
  battle_title: string
  current_group_score: number
  last_update: string
  submission_deadline: string
  students: Student[]
  API_Token: string
  group_leader: string
}

export type CardInfo = CardInfoEducator | CardInfoStudent

export type DashboardResponse = {
  account_type: string
  notifications: NotificationDetails[]
  cards: CardInfo[]
}

export type StaticAnalysisResult = {
  name: string, 
  score: number
}

export type EvaluationResult = {
  tests_results: {}
  static_analysis_results: {}
  timeliness_score: number
  manual_assessment_score: number 
} 

export type Group = {
  id: string
  leader: GroupMember
  members: GroupMember[]
  pending_invites: PendingInvite[]
  evaluation_result: EvaluationResult
  repository: string
  API_Token: string
  last_update: string
  total_score: number
  done_manual_evaluation: boolean
}

export type SetGroupRepositoryRequest = {
  tournament_title: string, 
  group_id: string, 
  repository: string,
  group_leader: string
}

export type ScoringParameter = {
  name: string
  score: number
}

export type TestResult = {
  testName: string
  status: TestStatus
}

export type GroupMember = {
  id: string
  username: string
  email: string
}

// TODO: multi-invite
export type GroupInviteRequest = {
  tournament_title: string
  battle_title: string
  username: string
}

export type InviteCard = {
  invite_id: string
  sender: string, 
  tournament_id: string
  tournament_title: string, 
  battle_title: string
}

export type InviteStatusUpdateRequest = {
  invite_id: string
  tournament_id: string
  accepted: boolean
}

// TODO: multi-invite
export type ManagerInviteRequest = {
  tournament_title: string
  username: string
}

export type PendingInvite = {
  id: string,
  username: string
}

export type Notification = {
  id: string
  message: string
  creation_date: string
  is_closed: boolean
}

export type NotificationDetails = {
  id: string
  message: string
  type: NotificationType
  context_url?: string
  display_type?: string 
}

export type BattleInfo = {
  tournament_title: string
  battle_title: string
  is_open: boolean
  enrollment_deadline: string
  enrolled_groups: number
}

export type TournamentInfo = {
  tournament_title: string
  subscribed_students_count: number
  number_of_battles: number
  subscription_deadline: string
  educators: Educator[]
  is_open: boolean
}

export type User = {
  username: string
}
// No profile images
export type Educator = User
export type Student = User

export type Tournament = {
  id: string
  title: string
  educator_creator: string
  id_open: boolean
  subscribers: TournamentSubscriber[]
  educators: TournamentManager[]
  pending_invites: PendingInvite[]
  battles: Battle[]
  subscription_deadline: string
  rank_students: { username: string; score: number }[]
}

export type TournamentCreationRequest = {
  title: string
  subscription_deadline: string
  invited_managers: string[]
}

export type LeaderboardEntry = {
  name: string
  score: number
}

export type TournamentGetResponse = {
  is_open: boolean
  creator: string
  managers: string[]
  pending_invites: PendingInvite[]
  battles: BattleInfo[]
  leaderboard: LeaderboardEntry[]
  subscription_deadline: string
  already_subscribed: boolean
}

export type TournamentsListEntry = {
  title: string
  is_open: boolean
  subscription_deadline: string
  subscribed_students: number
  educators: string[]
}

export type TournamentManager = {
  id: string
  username: string
}

export type TournamentSubscriber = {
  id: string
  username: string
  email: string
  score: number
}

export type TournamentSubscriberRequest = {
  title: string
}

export type BattleData = {
  title: string
  repository: string
  description: string
  language: string
  evaluation_parameters: string[]
  manual_evaluation: boolean
  submission_deadline: string
  enrollment_deadline: string
  min_group_size: number
  max_group_size: number
  leaderboard: LeaderboardEntry[]
  group?: Group
  groups?: Group[]
}

export type GetBattleRequest = {
  tournament_title: string
  battle_title: string
}

export type SimpleUserProfile = {
  username: string
  profile_img_url?: string
}

export type ManualEvaluationRequest = {
  tournament_title: string, 
  battle_title: string,
  group_id: string,
  points: number
}