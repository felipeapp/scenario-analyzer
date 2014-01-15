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
	<c:if test="${atividadeExtensao.controleFluxo != null}">
		<c:forEach var="passo" items="${atividadeExtensao.controleFluxo.passos}" varStatus="loop">
			<c:set var="clsOrdem" value="${ loop.index > atividadeExtensao.controleFluxo.passoAtual ? 'proximoPasso' : ''}" />
			<c:set var="clsAtual" value="${ loop.index == atividadeExtensao.controleFluxo.passoAtual ? 'passoAtual' : ''}" />
	
			<li class="${clsOrdem} ${clsAtual}"> ${passo} </li>
		</c:forEach>
	</c:if>	
</ol>