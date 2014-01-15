<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/avaliacao_ead.js"></script>

<script type="text/javascript">
function copiarNotas(componente) {
	var el = getEl('item_1_comp_'+componente);
	var els = getEl(document).getChildrenByClassName('nota_' + componente);
	for (var i = 0; i < els.length; i++) {
		els[i].dom.value = el.dom.value;
	}	
	iniciar();
}
</script>

<f:view>
<f:subview id="menu">
<%@include file="/portais/tutor/menu_tutor.jsp" %>
</f:subview>

<h2><ufrn:subSistema /> > Avaliação Periódica</h2>

<h:outputText value="#{ fichaAvaliacaoEad.create }"/>
<c:set var="disciplinas" value="${ fichaAvaliacaoEad.disciplinas }"/>
<p align="center">
<strong>${ fichaAvaliacaoEad.discente.matricula } - ${ fichaAvaliacaoEad.discente.nome }</strong>
</p>
<div style="margin: 10px auto;">
${ fichaAvaliacaoEad.metodologia.cabecalhoFicha }
</div>

<h:form>
<table class="listagem">
	<caption>Avaliação</caption>
	
	<thead>
	<tr><th align="center">Itens a Avaliar</th>
	<c:forEach var="disc" items="${ disciplinas }">
		<th width="10%" align="center"><label title="${ disc.nome }">${ disc.codigo }</label></th>
	</c:forEach>
	</tr>	
	</thead>
	
	<tbody>
	<c:forEach var="item" items="${ fichaAvaliacaoEad.avaliacao.itens }" varStatus="loop">
	<tr>
		<td>${ item.nome }</td>
		<c:forEach var="nota" items="${ item.notas }">
			<td>
			<input type="text" onkeydown="return(formataValor(this, event, 1))" onblur="verificaNotaValida(this)" maxlength="4" size="3" class="nota_${ nota.componente.id }" id="item_${ loop.index + 1 }_comp_${ nota.componente.id }" name="nota_${ item.id }_${ nota.componente.id }" value="<fmt:formatNumber value="${ nota.nota }" pattern="#0.0"/>"/>
			<c:if test="${ loop.first }">
				<img src="${ctx}/img/arrow_down.png" alt="Copiar nota para os outros itens" title="Copiar nota para os outros itens" onclick="copiarNotas(${ nota.componente.id });" style="cursor: pointer;"/>
			</c:if>
			</td>		
		</c:forEach>
	</tr>
	</c:forEach>
	</tbody>
	
	<tfoot>
		<tr>
			<td>Nota da Semana: </td>
			<c:forEach var="disc" items="${ disciplinas }">
				<td>&nbsp;&nbsp;<span id="media_${ disc.id }" class="media"></span></td>
			</c:forEach>
		</tr>
	</tfoot>
	
</table>
<div style="font-size: 0.85em;">
<strong>Legenda:</strong><br/>
<c:forEach var="disciplina" items="${ disciplinas }">
	${ disciplina.codigo } - ${ disciplina.nome }<br/>	
</c:forEach>
</div>
<br/>
<p>
<strong>Observações:</strong><br/>
<h:inputTextarea value="#{ fichaAvaliacaoEad.avaliacao.observacoes }" rows="3" style="width: 97%"/>
</p>

<p align="center">
<br/>
<h:commandButton value="<< Voltar" action="#{ fichaAvaliacaoEad.voltar }"/>
<h:commandButton value="Avaliar" action="#{ fichaAvaliacaoEad.avaliar }"/>
<h:commandButton value="Cancelar" action="#{ fichaAvaliacaoEad.cancelar }" onclick="#{confirm }"/>
</p>

</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

<script type="text/javascript">
function verificaNotaValida(element) {
	var valor = parseFloat(element.value.replace(',','.'));
	if(isNaN(valor) && element.value != '') {
		alert('Nota invalida. As notas devem ser valores numericos.');
		element.value = '';
	}
	if (valor > 10.0) {
		alert('Nota invalida. As notas devem estar entre 0 e 10.');
		element.value = '';
	}
}
</script>