<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<a4j:keepAlive beanName="validacaoVinculo" />

	<h:form>
	<h2 class="title"><ufrn:subSistema /> > Validação de Vínculos de Ingressante</h2>
	
		<div class="descricaoOperacao">
			<b>Caro usuário,</b> 
			<br/><br/>
			Nesta tela é possível validar o vínculo de um discente ingressante.
			<br/><br/>
		</div>
		
	<table class="formulario" style="width: 90%">
		<caption>Dados do Discente</caption>
		<tr>
			<td></td>
			<th style="text-align: right;font-weight:bold;"> Discente: </th>
			<td style="text-align: left;"> ${validacaoVinculo.discenteSelecionado.pessoa.nome } </td>
		</tr>
		<tr>
			<td width="5%"></td>
			<th width="3%" style="text-align: right;font-weight:bold;">Matrícula:</th>
			<td style="text-align: left;">${validacaoVinculo.discenteSelecionado.matricula }</td>
		</tr>
		<tr>
			<td></td>
			<th style="text-align: right;font-weight:bold;"> Curso: </th>
			<td style="text-align: left;"> 	<h:outputText value="#{ validacaoVinculo.discenteSelecionado.curso.nome }" /> </td>
		</tr>
		<tr>
			<td></td>
			<th width="20%" style="text-align: right;font-weight:bold;"> Matriz Curricular: </th>
			<td style="text-align: left;"> <h:outputText value="#{ validacaoVinculo.discenteSelecionado.matrizCurricular.descricao }" /> </td>
		</tr>	
		<tfoot>
			<tr>
			<td colspan=3>
				<h:commandButton action="#{validacaoVinculo.confirmarVinculo}" value="Confirmar Vínculo" rendered="#{validacaoVinculo.confirmar}"/>
				<h:commandButton action="#{validacaoVinculo.desconfirmarVinculo}" value="Desconfirmar Vínculo" rendered="#{!validacaoVinculo.confirmar}"/>
				<h:commandButton value="<< Voltar" action="#{validacaoVinculo.voltar}"/>
				<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{validacaoVinculo.cancelar}"/>
			</td>
			</tr>
		</tfoot>
	</table>
	<br/>
			
	</h:form>
	
		
<br />
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
