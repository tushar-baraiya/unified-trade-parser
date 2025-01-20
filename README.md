# Unified Trade Parser

[![License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE)
[![Build](https://github.com/username/unified-trade-parser/actions/workflows/build.yml/badge.svg)]

**Unified Trade Parser (UTP)** is an open-source Java library designed to parse and unify trade data across brokers, crypto exchanges, and asset classes.

## Key Features
- Multi-broker and multi-asset support
- Unified data standardization
- Extensible and community-driven development
- Advanced analytics capabilities

## Getting Started

### Prerequisites
- Java 11 or higher
- Maven 3.6+

### Installation
Clone the repository and build with Maven:
```bash
git clone https://github.com/your-username/unified-trade-parser.git
cd unified-trade-parser
mvn clean install
```

### Usage
```java
import com.unifiedtradeparser.TradeParser;

public class Main {
    public static void main(String[] args) {
        TradeParser parser = new TradeParser();
        parser.parse("path/to/export/file.csv");
    }
}
```

## Contributing
We welcome contributions! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for details.

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
