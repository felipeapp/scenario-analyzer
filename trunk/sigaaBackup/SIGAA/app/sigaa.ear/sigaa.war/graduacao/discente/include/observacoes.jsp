<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>

<style>
	div.aviso { text-align: center; color: red; }
	span.info { color: #666; font-size: 0.8em }

	#observacoes p {
		margin: 5px 3px 2px;
		padding: 5px;
		text-indent: 15px;
		border-bottom: 1px solid #CCC;
		background: #F1F1F1;
	}
	
	#observacoes .usuario {
		text-align: right;
		margin: 0 5px 15px 5px;
		font-size: 0.9em;
		color: #555;
	}
	
	#observacoes .remocao {
		float: right;
		width: 50px;
	}
	
	#observacoes table.observacao {
		width: 98%;
		margin: 2px auto 2px;
		border-bottom: 1px solid #C4D2EB;
		background: #EFF3FA;
	}
</style>

<div id="observacoes">
	<c:set var="observacoes" value="${ historicoDiscente.observacoes }"/>
	<c:forEach var="obs" items="${observacoes}">
		<p> ${obs.observacao} </p>
		<div class="usuario">
			cadastrada por ${obs.registro.usuario.pessoa.nome} (${obs.registro.usuario.login})
			em <ufrn:format type="dataHora" name="obs" property="data" />
			<c:if test="${obs.movimentacao != null}">
			<br>
			através de um ${obs.movimentacao.tipoMovimentacaoAluno.descricao}
			referente a ${obs.movimentacao.anoReferencia}.${obs.movimentacao.periodoReferencia}
			</c:if>
		</div>
	</c:forEach>
</div>