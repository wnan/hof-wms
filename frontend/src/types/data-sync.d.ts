export type SyncStatus = "idle" | "running" | "success" | "failed";
export type SyncType = "incremental" | "full";
export type TriggerType = "manual" | "schedule";

export interface FieldMapping { source: string; target: string; }

export interface SyncTask {
  id: string;
  name: string;
  externalSystem: string;
  endpoint: string;
  authType: "none" | "basic" | "bearer" | "apikey";
  syncType: SyncType;
  triggerType: TriggerType;
  cron?: string;
  mappings: FieldMapping[];
  status: SyncStatus;
  lastRunAt?: string;
}

export interface SyncLog {
  id: string;
  taskName: string;
  startAt: string;
  endAt: string;
  count: number;
  status: SyncStatus;
  error?: string;
}
