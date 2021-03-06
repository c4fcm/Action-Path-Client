package org.actionpath.ui;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.actionpath.R;

/**
 * Activities containing this fragment MUST implement the {@link OnAnswerSelectedListener}
 * interface.
 */
public class IssueDefaultQuestionFragment extends AbstractIssueQuestionFragment {

    private static String TAG = IssueDefaultQuestionFragment.class.getName();

    private OnAnswerSelectedListener listener;

    public static IssueDefaultQuestionFragment newInstance() {
        IssueDefaultQuestionFragment fragment = new IssueDefaultQuestionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public IssueDefaultQuestionFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        Log.d(TAG, "Building Issue Question Fragment UI");
        View view = inflater.inflate(R.layout.fragment_issue_default_question, container, false);

        Button answerYes = (Button) view.findViewById(R.id.issue_detail_yes);
        answerYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAnswerSelected("yes");
            }
        });
        Button answerNo = (Button) view.findViewById(R.id.issue_detail_no);
        answerNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAnswerSelected("no");
            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnAnswerSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnAnswerSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

}
