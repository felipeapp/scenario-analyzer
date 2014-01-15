<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2>atividadeExtensao</h2><br>
	<h:outputText value="#{atividadeExtensao.create}"/>
		<table class=listagem>
		<tr>
			<caption class="listagem">Lista de atividade de Extensão</caption>
			
			<td>Área</td>
			<td>Unidade</td>
			<td>Data Inclusão</td>
			<td>Objetivo</td>
			<td>Metodologia</td>
			<td>Inicio Prevísto</td>
			<td>Termino Previsto</td>
			<td>Início Real</td>
			<td>Término Real</td>
			<td>ch Total</td>
			<td>Data Avaliação</td>
			<td>Data Avaliação Depto</td>
			<td>Data Avaliação Consepe</td>
			<td>Data Avaliação Centro</td>
			<td>Título</td>
			<td>Validação</td>
			<td>Servidor</td>
			<td>Área Temática 2</td>
			<td>Área Temática 1</td>
			<td>Tipo Atividade Extensão</td>
			<td>Tipo Região</td>
			<td>Membro Equipe Atividade Extensão</td>
			<td>Beneficiário Atividade Extensão</td>
			<td>Parceria Atividade Extensão</td>
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