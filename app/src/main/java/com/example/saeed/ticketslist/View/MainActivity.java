package com.example.saeed.ticketslist.View;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.saeed.ticketslist.Model.Price;
import com.example.saeed.ticketslist.Model.Ticket;
import com.example.saeed.ticketslist.R;
import com.example.saeed.ticketslist.Utilities.Network.Api;
import com.example.saeed.ticketslist.Utilities.Network.ApiClient;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String from = "DEL";
    private static final String to = "HYD";

    @BindView(R.id.root_layout)
    ConstraintLayout constraintLayout;

    @BindView(R.id.ticketList)
    RecyclerView recyclerView;

    Api apiService;
    List<Ticket> ticketList = new ArrayList<>();
    TicketAdapter adapter;
    CompositeDisposable disposable = new CompositeDisposable();

    Unbinder unbinder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        unbinder = ButterKnife.bind(this);
        setView();
        getTickets();
    }

    private void setView() {
        adapter = new TicketAdapter(MainActivity.this, ticketList, new TicketAdapter.TicketAdapterListener() {
            @Override
            public void onTicketSelected(Ticket contact) {

            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void getTickets() {
        apiService = ApiClient.getClient().create(Api.class);
        ConnectableObservable<List<Ticket>> TicketsObservable = getObservableTickets(from , to).replay();

        disposable.add(TicketsObservable
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeWith(new DisposableObserver<List<Ticket>>() {

            @Override
            public void onNext(List<Ticket> tickets) {
                Log.d("saieddd", "onNext: " + tickets.size());
                ticketList.clear();
                ticketList.addAll(tickets);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        }));

        disposable.add(
                TicketsObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<List<Ticket>, ObservableSource<Ticket>>() {

                    @Override
                    public ObservableSource<Ticket> apply(List<Ticket> tickets) throws Exception {
                        return Observable.fromIterable(tickets);
                    }
                })
                .flatMap(new Function<Ticket, ObservableSource<Ticket>>() {
                    @Override
                    public ObservableSource<Ticket> apply(Ticket ticket) throws Exception {
                        return getPrices(ticket);
                    }
                })
                .subscribeWith(new DisposableObserver<Ticket>() {
                    @Override
                    public void onNext(Ticket ticket) {
                        int position = ticketList.indexOf(ticket);
                        if(position == -1){
                            return;
                        }
                        else {
                            ticketList.set(position,ticket);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                })
        );

        TicketsObservable.connect();
    }

    private Observable<List<Ticket>> getObservableTickets(String from, String to) {
        return apiService.searchTickets(from,to)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private ObservableSource<Ticket> getPrices(final Ticket ticket) {
        return apiService.getPrice(ticket.getFlightNumber(),ticket.getFrom(),ticket.getTo())
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<Price, Ticket>() {
                    @Override
                    public Ticket apply(Price price) throws Exception {
                        ticket.setPrice(price);
                        return ticket;
                    }
                });
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        disposable.dispose();
    }
}
