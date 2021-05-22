<template>
  <div class="open-answer-create">
      <v-container fluid>
        <v-row>
            <v-col
                cols="12"
                md="12"
            >
                <v-textarea
                    v-model="sQuestionDetails.correctAnswer"
                    name="input-7-1"
                    :rules="[v => !!v || 'You must type an answer']"
                    data-cy="QuestionCorrectAnswer"
                    required
                    label="Correct Answer"
                    value=""
                    hint=""
                ></v-textarea>
            </v-col>
        </v-row>
      </v-container>
  </div>
</template>

<script lang="ts">
import OpenAnswerQuestionDetails from '@/models/management/questions/OpenAnswerQuestionDetails';
import { Component, Vue, Prop, Watch, PropSync } from 'vue-property-decorator';

@Component({
  components: {
  }
})
export default class OpenAnswerCreate extends Vue {
  @PropSync('questionDetails', { type: OpenAnswerQuestionDetails })
  sQuestionDetails!: OpenAnswerQuestionDetails;
  @Prop({ default: true }) readonly readonlyEdit!: boolean;
}
</script>

<style lang="scss">
</style>
