package osahner.repository

import org.springframework.data.jpa.repository.JpaRepository
import osahner.domain.Address

interface AddressRepository : JpaRepository<Address, Int>
