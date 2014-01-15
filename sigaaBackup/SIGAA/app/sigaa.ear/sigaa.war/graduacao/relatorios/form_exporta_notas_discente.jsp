<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

 	<h2> <ufrn:subSistema /> > Alunos > Exportar Planilha de Notas do Semestre </h2>

	<h:form prependId="false">
		
		<table align="center" class="formulario" width="90%">
			<caption>Filtro para exportação</caption>
			
			<tbody>
				<tr>
					<th width="30%"> <b>Curso:</b> </th>
					<td>
						<h:outputText value="#{exportarNotasDiscente.cursoAtualCoordenacao.descricao}"/>	
					</td>
				</tr>
								
				<tr>
					<th width="30%" class="obrigatorio"> Ano-Período: </th>
					<td>
						<h:inputText value="#{exportarNotasDiscente.ano}" id="Ano" size="4" maxlength="4" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }" /> - 
						<h:inputText id="Período" value="#{exportarNotasDiscente.periodo}" size="1" maxlength="1" onkeyup="return formatarInteiro(this);"/> 
					</td>
				</tr>
			</tbody>
		
			<tfoot>
				<tr>
					<td colspan="2" align="center">
						<h:commandButton value="Gerar Arquivo" id="gerarArquivo" action="#{exportarNotasDiscente.gerarArquivo}" />
						<h:commandButton value="Cancelar" id="cancelar"	action="#{exportarNotasDiscente.cancelar}" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
			
		</table>
		
	</h:form>
	<br clear="all"/>
	
	<center>
		<h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>