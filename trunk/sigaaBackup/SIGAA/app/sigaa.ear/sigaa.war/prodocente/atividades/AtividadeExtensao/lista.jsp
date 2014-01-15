<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2>atividadeExtensao</h2><br>
	<h:outputText value="#{atividadeExtensao.create}"/>
		<table class=listagem>
		<tr>
			<caption class="listagem">Lista de atividade de Extens�o</caption>
			
			<td>�rea</td>
			<td>Unidade</td>
			<td>Data Inclus�o</td>
			<td>Objetivo</td>
			<td>Metodologia</td>
			<td>Inicio Prev�sto</td>
			<td>Termino Previsto</td>
			<td>In�cio Real</td>
			<td>T�rmino Real</td>
			<td>ch Total</td>
			<td>Data Avalia��o</td>
			<td>Data Avalia��o Depto</td>
			<td>Data Avalia��o Consepe</td>
			<td>Data Avalia��o Centro</td>
			<td>T�tulo</td>
			<td>Valida��o</td>
			<td>Servidor</td>
			<td>�rea Tem�tica 2</td>
			<td>�rea Tem�tica 1</td>
			<td>Tipo Atividade Extens�o</td>
			<td>Tipo Regi�o</td>
			<td>Membro Equipe Atividade Extens�o</td>
			<td>Benefici�rio Atividade Extens�o</td>
			<td>Parceria Atividade Extens�o</td>
			<td colspan="2"></td>
		</tr>
			
		  <c:forEach items="${atividadeExtensao.all}" var="item">
			<tr>	
			  <td>${item.area}</td>
			  <td>${item.unidade}</td>
			  <td>${item.dataInclusao}</td>
			  <td>${item.objetivo}</td>
			  <td>${item.metodologia}</td>
			  <td>${item.inicioPrevisto}</td>
			  <td>${item.terminoPrevisto}</td>
			  <td>${item.inicioReal}</td>
			  <td>${item.terminoReal}</td>
			  <td>${item.chTotal}</td>
			  <td>${item.dataAvaliacao}</td>
			  <td>${item.dataAvaliavaoDepto}</td>
			  <td>${item.dataAvaliacaoConsepe}</td>
			  <td>${item.dataAvaliacaoCentro}</td>
			  <td>${item.titulo}</td>
			  <td>${item.validacao}</td>
			  <td>${item.servidor}</td>
			  <td>${item.areaTematica2}</td>
			  <td>${item.areaTematica1}</td>
			  <td>${item.tipoAtividadeExtensao}</td>
			  <td>${item.tipoRegiao}</td>
			  <td>${item.membroEquipeAtividadeExtensaoCollection}</td>
			  <td>${item.beneficiarioAtividadeExtensaoCollection}</td>
			  <td>${item.parceriaAtividadeExtensaoCollection}</td>
			  <td  width=20>
				<h:form>
 				 <input type="hidden" value="${item.id}" name="id"/>
				 <h:commandButton image="/img/alterar.gif" value="Alterar" action="#{atividadeExtensao.atualizar}"/>
				</h:form>
			  </td>
			  <td  width=25>
				<h:form>
				 <input type="hidden" value="${item.id}" name="id"/>
				 <h:commandButton image="/img/delete.gif" alt="Remover" action="#{atividadeExtensao.remover}"/>
				</h:form>
			  </td>

			</tr>
		</c:forEach>
</table>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>