let products, stores = []
let currentStore = {}
const ROLE_CLIENT = 'client';
const ROLE_ADMIN = 'admin';




function loadValuesSections() {
    const storesTab = document.getElementById('stores-tab');
    const productsTab = document.getElementById('products-tab');
    
    if (storesTab.classList.contains('active-tab')) {
        loadCategoriesFromStores();
        if(getUserRole() === ROLE_ADMIN) {

            if(!document.getElementById('save-store-button')) {
                const saveStoreButton = document.createElement('button');
                saveStoreButton.textContent = 'Add New Store'; 
                saveStoreButton.id = 'save-store-button';
                saveStoreButton.addEventListener('click', () => {
                    const saveStoreSection = document.getElementById('stores-section-save');
                    saveStoreSection.style.display = saveStoreSection.style.display === 'none' ? 'block' : 'none';
                });
                document.getElementById("stores-section-header").appendChild(saveStoreButton);
            }
            
            
        }

    }else if (productsTab.classList.contains('active-tab')) {
        loadLabelsFromProducts();
    }
  
}

function loadCategoriesFromStores() {
    const select = document.getElementById('find-by-categories');

    fetch('/api/stores/findAllUniqueCategories')
    .then(response => {
        if (!response.ok) throw new Error('Error getting all unique categories for stores');

        return response.json();
    })
    .then(categories => {
        select.innerHTML = '<option value="all" selected>Select a category</option>';
        
        categories.forEach(category => {
            const option = document.createElement('option');
            option.value = category;
            option.textContent = category;
            select.appendChild(option);
        });
        
        select.addEventListener('change', (event) => {
            loadStoresByCategory(event.target.value);
        });
        loadStoresByCategory('all');
    })
    .catch(error => {
        console.error('Error:', error);
    });
}

function loadStoresByCategory(category) {
    const url = category === 'all' 
        ? '/api/stores/findAll' 
        : `/api/stores/findAllByCategory?category=${encodeURIComponent(category)}`;

    fetch(url)
    .then(response => {
        if (!response.ok) throw new Error('Error getting stores');
        return response.json();
    })
    .then(stores => {
        const container = document.getElementById('stores-container');
        container.innerHTML = '';
        const isAdmin = getUserRole() === ROLE_ADMIN;
        
        stores.forEach(store => {
            const storeData = JSON.stringify(store)
            .replace(/'/g, "\\'")
            .replace(/"/g, '&quot;');
            const adminButtons = isAdmin ? `
                <div class="admin-actions mt-2">
                    <button id="edit-store-management-button" class="btn btn-sm btn-primary edit-store" data-store='${storeData}'>
                        <i class="fas fa-edit"></i> Edit
                    </button>
                    <button id="delete-store-button" class="btn btn-sm btn-danger delete-store" data-store='${storeData}'>
                        <i class="fas fa-trash"></i> Delete
                    </button>
                </div>
            ` : '';
            
            const storeCard = `
                <div class="col-md-4 mb-4">
                    <div class="card store-card-${store.label.replaceAll(" ", "_")}">
                        <div class="card-body">
                            <h5 class="card-title">${store.label}</h5>
                            <p class="card-text">
                                <strong>Category:</strong> ${store.category}<br>
                                <strong>Label:</strong> ${store.label}<br>
                                <strong>Clients:</strong> ${store.clients?.length || 0}<br>
                                <strong>Products:</strong> ${store.products?.length || 0}
                            </p>
                            <button class="btn btn-sm btn-info view-products" data-store='${storeData}'>
                                <i class="fas fa-eye"></i> Products
                            </button>
                            ${adminButtons}
                        </div>
                    </div>
                    <div class="card store-card-edit-${store.label.replaceAll(" ", "_")}" style="display: none;">
                        <div class="card-body">
                            <h5 class="card-title">${store.label}</h5>
                            <p class="card-text">
                                <strong>Category:</strong><input id="category-store-edit" type="text" class="form-input" value="${store.category}"/>
                                <strong>Label:</strong><input id="label-store-edit" type="text" class="form-input" value="${store.label}"/>
                                <strong>Clients:</strong> ${store.clients?.length || 0}<br>
                                <strong>Products:</strong> ${store.products?.length || 0}
                            </p>
                            <button id="edit-store-button" class="btn btn-sm btn-info edit-store" data-store='${storeData}'>
                                <i class="fas fa-edit"></i> Edit
                            </button>
                            <button id="edit-store-cancel-button" class="btn btn-sm btn-info edit-store" data-store='${storeData}'>
                                <i class="fas fa-xmark"></i> Cancel
                            </button>
                        </div>
                    </div>
                </div>`;
            container.insertAdjacentHTML('beforeend', storeCard);
            
        });


        document.querySelectorAll('.view-products').forEach(button => {
            button.addEventListener('click', (e) => {
                const store = JSON.parse(e.currentTarget.dataset.store.replace(/&quot;/g, '"')); 
                currentStore = store;
                showProductsModal(store);
            });
        });
        document.querySelectorAll('#edit-store-management-button').forEach(button => {
            button.addEventListener('click', (e) => {
                const store = JSON.parse(e.currentTarget.dataset.store.replace(/&quot;/g, '"')); 
                console.log(store);
                editStoreManagement(store);                
            });
        });
        document.querySelectorAll('#edit-store-cancel-button').forEach(button => {
            button.addEventListener('click', (e) => {
                const store = JSON.parse(e.currentTarget.dataset.store.replace(/&quot;/g, '"')); 
                cancelEditStoreManagement(store);                
            });
        });
        document.querySelectorAll('#edit-store-button').forEach(button => {
            button.addEventListener('click', (e) => {
                const store = JSON.parse(e.currentTarget.dataset.store.replace(/&quot;/g, '"')); 
                editStore(store);                
            });
        });
        document.querySelectorAll('#delete-store-button').forEach(button => {
            button.addEventListener('click', (e) => {
                const store = JSON.parse(e.currentTarget.dataset.store.replace(/&quot;/g, '"')); 
                deleteStore(store);               
            });
        });
    })
    .catch(error => {
        console.error('Error:', error);
    });
}

function showProductsModal(store) {
    const modal = new bootstrap.Modal(document.getElementById('productsModal'));
    const modalTitle = document.getElementById('productsModalLabel');
    const tableBody = document.getElementById('productsTableBody');
    const addProductBtn = document.getElementById('save-product-btn');
    const productFormSection = document.getElementById('product-section-save');
    
    modalTitle.textContent = `Products from ${store.label}`;
    tableBody.innerHTML = '';
    productFormSection.style.display = 'none';
    
    const isAdmin = getUserRole() === ROLE_ADMIN;
    
    if(store.products && store.products.length > 0) {
        store.products.forEach(product => {
            createProductRow(product, store, isAdmin, tableBody);
        });
    } else {
        tableBody.innerHTML = '<tr><td colspan="5" class="text-center">No hay productos disponibles</td></tr>';
    }
    
    modal.show();
    
    if(isAdmin) {
        addProductBtn.addEventListener('click', () => {
            const saveProductSection = document.getElementById('product-section-save');
            saveProductSection.style.display = saveProductSection.style.display === 'none' ? 'block' : 'none';
            document.getElementById('label-product-register').value = '';
            document.getElementById('name-product-register').value = '';
            document.getElementById('description-product-register').value = '';
            document.getElementById('price-product-register').value = '';
        });
    }
}

function createProductRow(product, store, isAdmin, tableBody) {
    const row = document.createElement('tr');
    row.dataset.productLabel = product.label;

    const productData = JSON.stringify(product)
        .replace(/'/g, "\\'")
        .replace(/"/g, '&quot;');
    
    row.innerHTML = `
            <td>${product.label}</td>
            <td>${product.name}</td>
            <td>${product.description}</td>
            <td>$${product.price.toFixed(2)}</td>
            <td>
                ${isAdmin ? `
                <button id="edit-product-button" class="btn btn-sm btn-warning edit-product" data-product='${productData}'>
                    <i class="fas fa-edit"></i>
                </button>
                <button id="delete-product-button" class="btn btn-sm btn-danger delete-product" data-product='${productData}'>
                    <i class="fas fa-trash"></i>
                </button>
                ` : 'No Available'}
            </td>
    `;
    
    tableBody.appendChild(row);
    
    if(isAdmin) {
        row.querySelector('#edit-product-button')?.addEventListener('click', (event) => {
            const product = JSON.parse(event.currentTarget.dataset.product.replace(/&quot;/g, '"')); 
            const productSectionEdit = document.getElementById('product-section-edit');
            
            document.getElementById('label-product-edit').value = product.label || '';
            document.getElementById('name-product-edit').value = product.name || '';
            document.getElementById('description-product-edit').value = product.description || '';
            document.getElementById('price-product-edit').value = product.price || '';
            
            productSectionEdit.style.display = productSectionEdit.style.display === 'none' ? 'block' : 'none'; 
            
            productSectionEdit.dataset.editingProduct = JSON.stringify(product);
            const form = document.getElementById('product-form-edit');
            form.removeEventListener('submit', editProduct);
            form.addEventListener('submit', editProduct);

        });
        
        row.querySelector('#delete-product-button')?.addEventListener('click', (event) => {
            const product = JSON.parse(event.currentTarget.dataset.product.replace(/&quot;/g, '"')); 

            deleteProduct(product, store);
        });
    }
}

function loadLabelsFromProducts () {
    const select = document.getElementById('find-by-labels');

    fetch('/api/products/findAllUniqueLabels')
    .then(response => {
        if (!response.ok) throw new Error('Error getting all unique labels for products');
        return response.json();
    })
    .then(labels => {
        select.innerHTML = '<option value="all" selected>Select a label</option>';
        
        labels.forEach(category => {
            const option = document.createElement('option');
            option.value = category;
            option.textContent = category;
            select.appendChild(option);
        });

        select.addEventListener('change', (event) => {
            loadProductsByLabel(event.target.value);
        });
        
        loadProductsByLabel('all');
    })
    .catch(error => {
        console.error('Error:', error);
    });
}

function loadProductsByLabel (label) {
    const url = label === 'all' 
        ? '/api/products/findAll' 
        : `/api/products/findAllByLabel?label=${encodeURIComponent(label)}`;

    fetch(url)
    .then(response => {
        if (!response.ok) throw new Error('Error getting products');
        return response.json();
    })
    .then(products => {
        const container = document.getElementById('products-container');
        container.innerHTML = '';
        
        products.forEach(product => {
            const productCard = `
                <div class="col-md-4">
                    <div class="card product-card">
                        <div class="card-body">
                            <h5 class="card-title">Products from ${product.label}</h5>
                            <p class="card-text">
                                <strong>Name:</strong> ${product.name}<br>
                                <strong>Description:</strong> ${product.description}<br>
                                <strong>Price:</strong> ${product.price}<br>
                            </p>
                        </div>
                    </div>
                </div>`;
            container.insertAdjacentHTML('beforeend', productCard);
        });
    })
    .catch(error => {
        console.error('Error:', error);
    });
}

function saveStore (event) {
    event.preventDefault();
    const storeData = {
        category : document.getElementById('category-store-register').value,
        label : document.getElementById('label-store-register').value,
        products : [],
        clients : []
    };

    fetch('http://localhost:8080/api/stores/save', {
        method : 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body : JSON.stringify(storeData),
    })
    .then(response => {
        if(!response.ok) throw new Erorr('Error saving store.')
        return response.json()
    })
    .then(store => {
        event.target.reset();
        stores.push(store);
        console.log(store);
        loadCategoriesFromStores();
    })
    .catch(error => {
        console.error('Error Storing: ', error);
    });
    
}

function editStore (store) {
    if(document.getElementById('category-store-edit') && document.getElementById('label-store-edit')) {
        if(document.querySelector(`.store-card-edit-${store.label.replaceAll(" ", "_")}`).style.display === 'block'){
            const newCategory = document.getElementById('category-store-edit').value;
            const newLabel = document.getElementById('label-store-edit').value;

            fetch(`http://localhost:8080/api/stores/updateStore?oldCategory=${store.category}&oldLabel=${store.label}&newCategory=${newCategory}&newLabel=${newLabel}`, {
                method : 'PUT'
            })
            .then(response => {
                if(!response) throw new Error('Error Updating Store')
                return response.json();
            })
            .then(s => {
                console.log('Store updated to:',s)
                location.reload();

            }).catch(error => {
                console.log("Error: ", error)
            });
        }
    }
}

function deleteStore (store) {
    fetch(`http://localhost:8080/api/stores/delete?category=${store.category}&label=${store.label}`, {
        method : 'DELETE'
    })
    .then(response => {
        if(!response) throw new Error('Error Updating Store')
        console.log(response.json());
        loadCategoriesFromStores();
    })
    .catch(error => {
        console.log("Error: ", error)
    });
    
}

function saveProduct (event,) {
    event.preventDefault();
    const newStore = {};
    const productData = {
        label : document.getElementById('label-product-register').value,
        name : document.getElementById('name-product-register').value,
        description : document.getElementById('description-product-register').value,
        price : document.getElementById('price-product-register').value
    };

    //ADD TO THE STORE
    fetch(`http://localhost:8080/api/stores/updateProducts?category=${currentStore.category}&label=${currentStore.label}`, {
        method : 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body : JSON.stringify(productData),
    })
    .then(response => {
        if(!response.ok) throw new Erorr('Error saving store.')
        return response.json();
    })
    .then(store => {
        console.log(store);
        newStore = store;

    })
    .catch(error => {
        console.error('Error saving product to store: ', error);
    });

    //ADD TO PRODUCTS TABLE
    fetch('http://localhost:8080/api/products/save', {
        method : 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body : JSON.stringify(productData),
    })
    .then(response => {
        if(!response.ok) throw new Erorr('Error saving store.')
        return response.json();
    })
    .then(product => {
        console.log(product);
        loadStoresByCategory(document.getElementById('find-by-categories').value);
        showProductsModal(newStore);
        location.reload();
    })
    .catch(error => {
        console.error('Error Saving Products: ', error);
    });
}

function editProduct(event) {
    event.preventDefault();
    
    const productSectionEdit = document.getElementById('product-section-edit');
    const originalProduct = JSON.parse(productSectionEdit.dataset.editingProduct);
    const newProduct = {
        label: document.getElementById('label-product-edit').value,
        name: document.getElementById('name-product-edit').value,
        description: document.getElementById('description-product-edit').value,
        price: parseFloat(document.getElementById('price-product-edit').value)
    };

    //Update One Product in store
    fetch(`http://localhost:8080/api/stores/updateOneProduct?category=${currentStore.category}&label=${currentStore.label}&oldProductLabel=${originalProduct.label}&oldProductName=${originalProduct.name}`, {
        method : "PUT",
        headers: {
            "Content-Type":"application/json"
        },
        body : JSON.stringify(newProduct)
    })
    .then(response => {
        if(!response.ok) throw new Erorr('Error editing one product of store.')
        return response.json();
    })
    .then(store => {
        console.log("Store updated: ", store);
        currentStore = store;
        productSectionEdit.style.display = 'none';
        loadCategoriesFromStores();
        bootstrap.Modal.getInstance(document.getElementById('productsModal')).hide();
        showProductsModal(store);
    })
    .catch(error => {
        console.error('Error editing one product of store: ', error);
    });
    
    //Update in products table
    fetch(`http://localhost:8080/api/products/updateProduct?oldLabel=${originalProduct.label}&oldName=${originalProduct.name}`, {
        method : "PUT", 
        headers : {
            "Content-Type":"application/json"
        },
        body : JSON.stringify(newProduct)
    })
    .then(response => {
        if(!response.ok) throw new Erorr('Error updating product in table products.')
        return response.json();
    })
    .then(p => {
        console.log("Product updated: ", p);
    })
    .catch(error => {
        console.error('Error updating product in table products: ', error);
    });

    
}

function deleteProduct (product, store) {

    fetch(`http://localhost:8080/api/stores/deleteOneProduct?category=${store.category}&label=${store.label}&productLabel=${product.label}&productName=${product.name}`, {
        method : "DELETE"
    })
    .then(response => {
        if(!response) throw new Error("Error deleting one product from store.")
        return response.json();
    })
    .then(s => {
        console.log(s);
        currentStore = s;
        loadCategoriesFromStores();
        bootstrap.Modal.getInstance(document.getElementById('productsModal')).hide();
        showProductsModal(s);
    })
    .catch(error => {
        console.error('Error deleting one product from store: ', error);
    });

}


function showSection(sectionId) {
    document.querySelectorAll('.content-section').forEach(section => {
        section.classList.remove('active');
    });
    
    document.getElementById(`${sectionId}-section`).classList.add('active');
    
    document.querySelectorAll('.nav-link').forEach(tab => {
        tab.classList.remove('active-tab');
    });
    document.getElementById(`${sectionId}-tab`).classList.add('active-tab');
    loadValuesSections();
}

function getUserRole() {
    const userData = localStorage.getItem('user');
    if (!userData) return null;

    try {
        if (typeof userData === 'object') return userData.role;
        
        const user = typeof userData === 'string' ? JSON.parse(userData) : userData;
        console.log("User data:", user);
        return user?.role || null;
    } catch (e) {
        console.error('Error parsing user data:', e);
        return null;
    }
}

function editStoreManagement (store) {
    document.querySelector(`.store-card-${store.label.replaceAll(" ", "_")}`).style.display = 'none';
    document.querySelector(`.store-card-edit-${store.label.replaceAll(" ", "_")}`).style.display = 'block';
}

function cancelEditStoreManagement (store) {
    document.querySelector(`.store-card-${store.label.replaceAll(" ", "_")}`).style.display = 'block';
    document.querySelector(`.store-card-edit-${store.label.replaceAll(" ", "_")}`).style.display = 'none';
}
function closeSidebar() {
    document.getElementById('store-products-sidebar').style.width = '0';
    document.getElementById('product-stores-sidebar').style.width = '0';
}


function logout() {
    localStorage.clear();
    window.location.href = '/';
}

loadValuesSections();