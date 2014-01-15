<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<style>
	#passos {
		width: 300px;
		list-style: decimal;
	}

	#passos li.passoAtual {
		font-weight: bold;
	}

	#passos li.proximoPasso {
		color: #888;
	}
</style>

<ol id="passos">
	<c:forEach var="passo" items="${projetoMonitoria.controleFluxo.passos}" varStatus="loop">
		<c:set var="clsOrdem" value="${ loop.index > projetoMonitoria.controleFluxo.passoAtual ? 'proximoPasso' : ''}" />
		<c:set var="clsAtual" value="${ loop.index == projetoMonitoria.controleFluxo.passoAtual ? 'passoAtual' : ''}" />

		<li class="${clsOrdem} ${clsAtual}">${passo}</a> </li>
	</c:forEach>
	
</ol>