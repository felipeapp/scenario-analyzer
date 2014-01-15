<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<script type="text/javascript" src="/shared/loadScript?src=javascript/jquery/jquery.maskedinput-1.2.2.min.js" ></script>
<f:view>
	<h:messages showDetail="true" />
	<h2> <ufrn:subSistema/> > Relatório dos Programas que não usaram processos seletivos on-line</h2>
	<h:form>
		<table class="formulario" width="35%">
			<caption class="formulario">Dados da Busca</caption>
			<tbody>
				<tr align="right">
					<th class="required" width="50%">Ano-Período:</th>
					<td align="left" width="2%"><h:inputText value="#{relatorioPrograma.ano}" size="4" maxlength="4" id="ano" onkeyup="return formatarInteiro(this);"/></td>
					<td align="left">.<h:inputText value="#{relatorioPrograma.periodo}" size="1" maxlength="1" id="periodo" onkeyup="return formatarInteiro(this);"/></td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="Gerar Relatório" action="#{relatorioPrograma.relatorioProgramasNFezProcessoSeletivo}" /> 
						<h:commandButton value="Cancelar" action="#{relatorioPrograma.cancelar}" />
					</td>
				</tr>
			</tfoot>
		</table>
		<rich:jQuery selector="#ano" query="mask('9999')" />
		<rich:jQuery selector="#periodo" query="mask('9')" />
	</h:form>
 <br />
 <center>
 <h:graphicImage url="/img/required.gif"/>
 <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
 </center>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>