<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2> <ufrn:subSistema /> > Andamentos dos cursos </h2>
<f:view>

  <div id="ajuda" class="descricaoOperacao">
  	- Este relat�rio exibe o andamento dos cursos baseado na carga hor�ria cumprida. No formul�rio abaixo dever� ser informado o per�odo dos cursos para realizar a busca.
  </div>
  <h:form id="form">
	<table class="formulario" style="width: 30%" align="center">
		<caption> Informe os crit�rios de busca </caption>
		<tbody>
		<tr>
			<th class="obrigatorio">Ano Inicial:</th>
			<td>
				<h:inputText value="#{relatoriosLato.anoInicial}" id="ano" size="6" maxlength="4" 
					 converter="#{ intConverter }" onkeyup="return formatarInteiro(this);"/>
			</td>
		</tr>
	
		<tr>
			<th class="obrigatorio">Ano Final:</th>
			<td>
				<h:inputText value="#{relatoriosLato.ano}" id="anoInicial" size="6" maxlength="4"  
					 converter="#{ intConverter }" onkeyup="return formatarInteiro(this);"/>
			</td>
		</tr>
	
		</tbody>
		<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton id="gerarRelatorio" value="Gerar Relat�rio" action="#{relatoriosLato.gerarRelatorioAndamentoCursos}" /> 
					<h:commandButton id="cancelar" value="Cancelar" action="#{relatoriosLato.cancelar}" onclick="#{confirm}"/>
				</td>
			</tr>
		</tfoot>
	</table>
  </h:form>

	<br />
	<center>
		<h:graphicImage url="/img/required.gif" style="vertical-align: top;" />
		<span class="fontePequena"> Campos de preenchimento obrigat�rio. </span>
	</center>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>