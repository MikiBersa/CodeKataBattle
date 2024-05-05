import { NotificationType, TestStatus, type NotificationContext, type StaticAnalysisResult, type TestResult } from "./custom_types"

export const EvalParameters = ['Quality', 'Security', 'Reliability']
export const ImplementedLanguages = [
  {
    display: 'Java 17 (or previous)',
    val: 'java 17'
  }
]

export function toDate(date_string: string): Date {
  return new Date(Date.parse(date_string))
}

export function formatDate(date: string): string
export function formatDate(date: Date): string
export function formatDate(date: string | Date): string {
  const _date: Date = date instanceof Date ? date : toDate(date)

  const formattedDate = `${_date.toLocaleDateString()} ${_date.toLocaleTimeString([], {
    hour: '2-digit',
    minute: '2-digit'
  })}`

  return formattedDate
}

export function dateExpired(date: string): boolean
export function dateExpired(date: Date): boolean
export function dateExpired(date: string | Date): boolean {
  const _date: Date = date instanceof Date ? date : toDate(date)
  return _date < new Date()
}

export function mapStaticResultsToObjects(results: Object): StaticAnalysisResult[] {
  const converted = Object.entries(results).map(([name, score]) => ({
    name: name, 
    score: score
  }));

  return converted; 
}

export function mapTestResultsToObjects(results: Object): TestResult[] {
  const converted = Object.entries(results).map(([name, status]) => ({
    testName: name,
    status: status as TestStatus
  }));

  return converted; 
}

// not pretty, just for aesthetics.
// NOTE: Context is not implemented at the moment...
export function mapNotificationType(type: NotificationType): NotificationContext {
  switch(type) {
    case NotificationType.GROUP_REMOVED_FROM_BATTLE: return {
      display: "Failed to enroll", 
      context_url(tournament_title, battle_title) {
          return `/battle?tournament=${tournament_title}&battle=${battle_title}`; 
      },
    }
    case NotificationType.INVITE_STATUS_UPDATE: return {
      display: "Invite update", 
      context_url(tournament_title, _) {
          return `/tournament?title=${tournament_title}`; 
      },
    }
    case NotificationType.NEW_BATTLE: return {
      display: "New battle", 
      context_url(tournament_title, battle_title) {
          return `/battle?tournament=${tournament_title}&battle=${battle_title}`; 
      },
    }
    case NotificationType.NEW_INVITE: return {
      display: "New Invite", 
      context_url(tournament_title, _) {
          return `/tournament?tournament=${tournament_title}`; 
      },
    }
    case NotificationType.NEW_RANK_AVAILABLE: return {
      display: "New rank available", 
      context_url(tournament_title, _) {
          return `/tournament?tournament=${tournament_title}`; 
      },
    }
    case NotificationType.NEW_REPOSITORY_INVITE: return {
      display: "Repository invite", 
      context_url(tournament_title, battle_title) {
          return `/battle?tournament=${tournament_title}&battle=${battle_title}`; 
      },
    }
    case NotificationType.NEW_TOURNAMENT: return {
      display: "New tournament", 
      context_url(tournament_title, _) {
          return `/tournament?title=${tournament_title}`; 
      },
    }
    case NotificationType.SUCCESSFUL_BATTLE_ENROLLMENT: return {
      display: "Enrolled in battle", 
      context_url(tournament_title, battle_title) {
          return `/battle?tournament=${tournament_title}&battle=${battle_title}`; 
      },
    }
    case NotificationType.SUCCESSFUL_TOURNAMENT_SUBSCRIPTION: return {
      display: "Subscribed to tournament", 
      context_url(tournament_title, _) {
          return `/tournament?title=${tournament_title}`; 
      },
    }
    case NotificationType.MANUAL_EVALUATION_REQUIRED: return {
      display: "Manual evaluation required", 
      context_url(tournament_title, battle_title) {
          return `/battle?tournament=${tournament_title}&battle=${battle_title}`; 
      },
    }
  } 
}