package com.ebookfrenzy.roomdemo.ui.main;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ebookfrenzy.roomdemo.Product;
import com.ebookfrenzy.roomdemo.R;
import com.ebookfrenzy.roomdemo.databinding.MainFragmentBinding;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Locale;

public class MainFragment extends Fragment {

    private MainViewModel mViewModel;
    private MainFragmentBinding binding;
    private ProductListAdapter adapter;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = MainFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        listenerSetup();
        observerSetup();
        recyclerSetup();
    }

    private void clearFields(){
        binding.productID.setText("");
        binding.productName.setText("");
        binding.productQuantity.setText("");
    }

    private void listenerSetup(){
        binding.addButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                String name = binding.productName.getText().toString();
                String quantity = binding.productQuantity.getText().toString();

                if(!name.equals("")&&!quantity.equals("")){
                    Product product = new Product(name, Integer.parseInt(quantity));
                    mViewModel.insertProduct(product);
                    clearFields();
                }else{
                    binding.productID.setText("Incomplete information");
                }
            }
        });

        binding.findButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mViewModel.findProduct(binding.productName.getText().toString());
            }
        });

        binding.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.deleteProduct(binding.productName.getText().toString());
                clearFields();
            }
        });
    }

    private void observerSetup(){
        mViewModel.getAllProducts().observe(getViewLifecycleOwner(),
                new Observer<List<Product>>(){
            @Override
            public void onChanged(@Nullable final List<Product> products){
                adapter.setProductList(products);
            }
        });

        mViewModel.getSearchResult().observe(getViewLifecycleOwner(),
                new Observer<List<Product>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onChanged(@Nullable final List<Product> products) {
                assert products != null;
                if(products.size() > 0){
                   binding.productID.setText(String.format(Locale.US, "%d", products.get(0).getId()));
                   binding.productName.setText(products.get(0).getName());
                   binding.productQuantity.setText(String.format(Locale.US, "%d", products.get(0).getQuantity()));
               }else{
                   binding.productID.setText("No Match");
               }
            }
        });
    }

    private void recyclerSetup(){
        adapter = new ProductListAdapter(R.layout.product_list_item);
        binding.productRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.productRecycler.setAdapter(adapter);
    }
}