<%@include file="/ava/cabecalho.jsp" %>

<style>
	span.situacao { color: #922; }
</style>

<f:view>
<%@include file="/ava/menu.jsp" %>
<h:form>


<fieldset>
<legend> DISCENTE SORTEADO </legend>
	
	<div class="infoAltRem">
		<h:graphicImage value="/img/ico-dados.png" style="border:0;"/>: Sortear Discente
	</div>

	<rich:panel id="discenteSorteado" style="text-align: center;" header="DISCENTE SORTEADO">
		<c:if test="${sorteioParticipantesMBean.discenteSorteado != null}">
			<div style="text-align: center; font-size: 14px;">
	 				<img src="/shared/verFotoIdentificacao?cpf=${sorteioParticipantesMBean.discenteSorteado.discente.pessoa.cpf_cnpj}&passkey=biosinfo" 
	 				width="180" height="180"/> <br><br>
					Nome: <h:outputText value="#{ sorteioParticipantesMBean.discenteSorteado.discente.pessoa.nome }" /> <br>
					Matrícula: <em><h:outputText value="#{ sorteioParticipantesMBean.discenteSorteado.discente.matricula }" /></em>
			</div>
		</c:if>
	</rich:panel>
	<div style="text-align: center;">
		<a4j:commandButton action="#{sorteioParticipantesMBean.sortearDiscentes}" alt="Sortear discente" 
		title="Sortear discente" id="botao" image="/img/ico-dados.png" reRender="discenteSorteado" style="border:0;"/>
	</div>
		
</fieldset>

<br/>

<c:set var="alunos" value="#{sorteioParticipantesMBean.discentesTurma}"/>
<fieldset>
<legend> Alunos (${ fn:length(alunos) })</legend>

<table class="participantes">
	<c:forEach items="${ alunos }" var="item" varStatus="loop">
		
		<c:if test="${loop.index % 2 == 0 }">
			<tr class="${loop.index % 4 == 0 ? 'odd' : 'even' }">
		</c:if>
		
		<td width="47">
			<img src="/shared/verFotoIdentificacao?cpf=${item.discente.pessoa.cpf_cnpj}&passkey=biosinfo" width="48" height="60"/>
		</td>
		<td valign="top" width="40%">
			<div style="font-size: 14px;">
				Nome: ${item.discente.pessoa.nome} <br/>
				Matrícula: <em>${item.discente.matricula}</em> <br/>
			</div> 
		</td>
			
	</c:forEach>
</table>
</fieldset>

</h:form>

</f:view>
<%@include file="/ava/rodape.jsp" %>
