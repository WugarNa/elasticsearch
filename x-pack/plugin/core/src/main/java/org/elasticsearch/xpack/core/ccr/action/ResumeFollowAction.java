/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */

package org.elasticsearch.xpack.core.ccr.action;

import org.elasticsearch.action.Action;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionRequestValidationException;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.common.ParseField;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.ObjectParser;
import org.elasticsearch.common.xcontent.ToXContentObject;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentParser;

import java.io.IOException;
import java.util.Objects;

import static org.elasticsearch.action.ValidateActions.addValidationError;

public final class ResumeFollowAction extends Action<AcknowledgedResponse> {

    public static final ResumeFollowAction INSTANCE = new ResumeFollowAction();
    public static final String NAME = "cluster:admin/xpack/ccr/resume_follow";

    public static final TimeValue MAX_RETRY_DELAY = TimeValue.timeValueMinutes(5);

    private ResumeFollowAction() {
        super(NAME);
    }

    @Override
    public AcknowledgedResponse newResponse() {
        return new AcknowledgedResponse();
    }

    public static class Request extends ActionRequest implements ToXContentObject {

        static final ParseField FOLLOWER_INDEX_FIELD = new ParseField("follower_index");
        static final ParseField MAX_BATCH_OPERATION_COUNT = new ParseField("max_batch_operation_count");
        static final ParseField MAX_CONCURRENT_READ_BATCHES = new ParseField("max_concurrent_read_batches");
        static final ParseField MAX_BATCH_SIZE = new ParseField("max_batch_size");
        static final ParseField MAX_CONCURRENT_WRITE_BATCHES = new ParseField("max_concurrent_write_batches");
        static final ParseField MAX_WRITE_BUFFER_COUNT = new ParseField("max_write_buffer_count");
        static final ParseField MAX_WRITE_BUFFER_SIZE = new ParseField("max_write_buffer_size");
        static final ParseField MAX_RETRY_DELAY_FIELD = new ParseField("max_retry_delay");
        static final ParseField POLL_TIMEOUT = new ParseField("poll_timeout");
        static final ObjectParser<Request, String> PARSER = new ObjectParser<>(NAME, Request::new);

        static {
            PARSER.declareString(Request::setFollowerIndex, FOLLOWER_INDEX_FIELD);
            PARSER.declareInt(Request::setMaxBatchOperationCount, MAX_BATCH_OPERATION_COUNT);
            PARSER.declareInt(Request::setMaxConcurrentReadBatches, MAX_CONCURRENT_READ_BATCHES);
            PARSER.declareField(
                    Request::setMaxBatchSize,
                    (p, c) -> ByteSizeValue.parseBytesSizeValue(p.text(), MAX_BATCH_SIZE.getPreferredName()),
                    MAX_BATCH_SIZE,
                    ObjectParser.ValueType.STRING);
            PARSER.declareInt(Request::setMaxConcurrentWriteBatches, MAX_CONCURRENT_WRITE_BATCHES);
            PARSER.declareInt(Request::setMaxWriteBufferCount, MAX_WRITE_BUFFER_COUNT);
            PARSER.declareField(
                    Request::setMaxWriteBufferSize,
                    (p, c) -> ByteSizeValue.parseBytesSizeValue(p.text(), MAX_WRITE_BUFFER_SIZE.getPreferredName()),
                    MAX_WRITE_BUFFER_SIZE,
                    ObjectParser.ValueType.STRING);
            PARSER.declareField(
                    Request::setMaxRetryDelay,
                    (p, c) -> TimeValue.parseTimeValue(p.text(), MAX_RETRY_DELAY_FIELD.getPreferredName()),
                    MAX_RETRY_DELAY_FIELD,
                    ObjectParser.ValueType.STRING);
            PARSER.declareField(
                    Request::setPollTimeout,
                    (p, c) -> TimeValue.parseTimeValue(p.text(), POLL_TIMEOUT.getPreferredName()),
                    POLL_TIMEOUT,
                    ObjectParser.ValueType.STRING);
        }

        public static Request fromXContent(final XContentParser parser, final String followerIndex) throws IOException {
            Request request = PARSER.parse(parser, followerIndex);
            if (followerIndex != null) {
                if (request.followerIndex == null) {
                    request.followerIndex = followerIndex;
                } else {
                    if (request.followerIndex.equals(followerIndex) == false) {
                        throw new IllegalArgumentException("provided follower_index is not equal");
                    }
                }
            }
            return request;
        }

        private String followerIndex;

        public String getFollowerIndex() {
            return followerIndex;
        }

        public void setFollowerIndex(String followerIndex) {
            this.followerIndex = followerIndex;
        }

        private Integer maxBatchOperationCount;

        public Integer getMaxBatchOperationCount() {
            return maxBatchOperationCount;
        }

        public void setMaxBatchOperationCount(Integer maxBatchOperationCount) {
            this.maxBatchOperationCount = maxBatchOperationCount;
        }

        private Integer maxConcurrentReadBatches;

        public Integer getMaxConcurrentReadBatches() {
            return maxConcurrentReadBatches;
        }

        public void setMaxConcurrentReadBatches(Integer maxConcurrentReadBatches) {
            this.maxConcurrentReadBatches = maxConcurrentReadBatches;
        }

        private ByteSizeValue maxBatchSize;

        public ByteSizeValue getMaxBatchSize() {
            return maxBatchSize;
        }

        public void setMaxBatchSize(ByteSizeValue maxBatchSize) {
            this.maxBatchSize = maxBatchSize;
        }

        private Integer maxConcurrentWriteBatches;

        public Integer getMaxConcurrentWriteBatches() {
            return maxConcurrentWriteBatches;
        }

        public void setMaxConcurrentWriteBatches(Integer maxConcurrentWriteBatches) {
            this.maxConcurrentWriteBatches = maxConcurrentWriteBatches;
        }

        private Integer maxWriteBufferCount;

        public Integer getMaxWriteBufferCount() {
            return maxWriteBufferCount;
        }

        public void setMaxWriteBufferCount(Integer maxWriteBufferCount) {
            this.maxWriteBufferCount = maxWriteBufferCount;
        }

        private ByteSizeValue maxWriteBufferSize;

        public ByteSizeValue getMaxWriteBufferSize() {
            return maxWriteBufferSize;
        }

        public void setMaxWriteBufferSize(ByteSizeValue maxWriteBufferSize) {
            this.maxWriteBufferSize = maxWriteBufferSize;
        }

        private TimeValue maxRetryDelay;

        public void setMaxRetryDelay(TimeValue maxRetryDelay) {
            this.maxRetryDelay = maxRetryDelay;
        }

        public TimeValue getMaxRetryDelay() {
            return maxRetryDelay;
        }

        private TimeValue pollTimeout;

        public TimeValue getPollTimeout() {
            return pollTimeout;
        }

        public void setPollTimeout(TimeValue pollTimeout) {
            this.pollTimeout = pollTimeout;
        }

        public Request() {
        }

        @Override
        public ActionRequestValidationException validate() {
            ActionRequestValidationException e = null;

            if (followerIndex == null) {
                e = addValidationError(FOLLOWER_INDEX_FIELD.getPreferredName() + " is missing", e);
            }
            if (maxBatchOperationCount != null && maxBatchOperationCount < 1) {
                e = addValidationError(MAX_BATCH_OPERATION_COUNT.getPreferredName() + " must be larger than 0", e);
            }
            if (maxConcurrentReadBatches != null && maxConcurrentReadBatches < 1) {
                e = addValidationError(MAX_CONCURRENT_READ_BATCHES.getPreferredName() + " must be larger than 0", e);
            }
            if (maxBatchSize != null && maxBatchSize.compareTo(ByteSizeValue.ZERO) <= 0) {
                e = addValidationError(MAX_BATCH_SIZE.getPreferredName() + " must be larger than 0", e);
            }
            if (maxConcurrentWriteBatches != null && maxConcurrentWriteBatches < 1) {
                e = addValidationError(MAX_CONCURRENT_WRITE_BATCHES.getPreferredName() + " must be larger than 0", e);
            }
            if (maxWriteBufferCount != null && maxWriteBufferCount < 1) {
                e = addValidationError(MAX_WRITE_BUFFER_COUNT.getPreferredName() + " must be larger than 0", e);
            }
            if (maxWriteBufferSize != null && maxWriteBufferSize.compareTo(ByteSizeValue.ZERO) <= 0) {
                e = addValidationError(MAX_WRITE_BUFFER_SIZE.getPreferredName() + " must be larger than 0", e);
            }
            if (maxRetryDelay != null && maxRetryDelay.millis() <= 0) {
                String message = "[" + MAX_RETRY_DELAY_FIELD.getPreferredName() + "] must be positive but was [" +
                    maxRetryDelay.getStringRep() + "]";
                e = addValidationError(message, e);
            }
            if (maxRetryDelay != null && maxRetryDelay.millis() > ResumeFollowAction.MAX_RETRY_DELAY.millis()) {
                String message = "[" + MAX_RETRY_DELAY_FIELD.getPreferredName() + "] must be less than [" + MAX_RETRY_DELAY +
                    "] but was [" + maxRetryDelay.getStringRep() + "]";
                e = addValidationError(message, e);
            }

            return e;
        }

        @Override
        public void readFrom(final StreamInput in) throws IOException {
            super.readFrom(in);
            followerIndex = in.readString();
            maxBatchOperationCount = in.readOptionalVInt();
            maxConcurrentReadBatches = in.readOptionalVInt();
            maxBatchSize = in.readOptionalWriteable(ByteSizeValue::new);
            maxConcurrentWriteBatches = in.readOptionalVInt();
            maxWriteBufferCount = in.readOptionalVInt();
            maxWriteBufferSize = in.readOptionalWriteable(ByteSizeValue::new);
            maxRetryDelay = in.readOptionalTimeValue();
            pollTimeout = in.readOptionalTimeValue();
        }

        @Override
        public void writeTo(final StreamOutput out) throws IOException {
            super.writeTo(out);
            out.writeString(followerIndex);
            out.writeOptionalVInt(maxBatchOperationCount);
            out.writeOptionalVInt(maxConcurrentReadBatches);
            out.writeOptionalWriteable(maxBatchSize);
            out.writeOptionalVInt(maxConcurrentWriteBatches);
            out.writeOptionalVInt(maxWriteBufferCount);
            out.writeOptionalWriteable(maxWriteBufferSize);
            out.writeOptionalTimeValue(maxRetryDelay);
            out.writeOptionalTimeValue(pollTimeout);
        }

        @Override
        public XContentBuilder toXContent(final XContentBuilder builder, final Params params) throws IOException {
            builder.startObject();
            {
                toXContentFragment(builder, params);
            }
            builder.endObject();
            return builder;
        }

        void toXContentFragment(final XContentBuilder builder, final Params params) throws IOException {
            builder.field(FOLLOWER_INDEX_FIELD.getPreferredName(), followerIndex);
            if (maxBatchOperationCount != null) {
                builder.field(MAX_BATCH_OPERATION_COUNT.getPreferredName(), maxBatchOperationCount);
            }
            if (maxBatchSize != null) {
                builder.field(MAX_BATCH_SIZE.getPreferredName(), maxBatchSize.getStringRep());
            }
            if (maxWriteBufferCount != null) {
                builder.field(MAX_WRITE_BUFFER_COUNT.getPreferredName(), maxWriteBufferCount);
            }
            if (maxWriteBufferSize != null) {
                builder.field(MAX_WRITE_BUFFER_SIZE.getPreferredName(), maxWriteBufferSize.getStringRep());
            }
            if (maxConcurrentReadBatches != null) {
                builder.field(MAX_CONCURRENT_READ_BATCHES.getPreferredName(), maxConcurrentReadBatches);
            }
            if (maxConcurrentWriteBatches != null) {
                builder.field(MAX_CONCURRENT_WRITE_BATCHES.getPreferredName(), maxConcurrentWriteBatches);
            }
            if (maxRetryDelay != null) {
                builder.field(MAX_RETRY_DELAY_FIELD.getPreferredName(), maxRetryDelay.getStringRep());
            }
            if (pollTimeout != null) {
                builder.field(POLL_TIMEOUT.getPreferredName(), pollTimeout.getStringRep());
            }
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Request request = (Request) o;
            return Objects.equals(maxBatchOperationCount, request.maxBatchOperationCount) &&
                    Objects.equals(maxConcurrentReadBatches, request.maxConcurrentReadBatches) &&
                    Objects.equals(maxBatchSize, request.maxBatchSize) &&
                    Objects.equals(maxConcurrentWriteBatches, request.maxConcurrentWriteBatches) &&
                    Objects.equals(maxWriteBufferCount, request.maxWriteBufferCount) &&
                    Objects.equals(maxWriteBufferSize, request.maxWriteBufferSize) &&
                    Objects.equals(maxRetryDelay, request.maxRetryDelay) &&
                    Objects.equals(pollTimeout, request.pollTimeout) &&
                    Objects.equals(followerIndex, request.followerIndex);
        }

        @Override
        public int hashCode() {
            return Objects.hash(
                    followerIndex,
                    maxBatchOperationCount,
                    maxConcurrentReadBatches,
                    maxBatchSize,
                    maxConcurrentWriteBatches,
                    maxWriteBufferCount,
                    maxWriteBufferSize,
                    maxRetryDelay,
                    pollTimeout);
        }
    }

}
