<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<%@page import="br.ufrn.sigaa.ensino.latosensu.dominio.PropostaCursoLato"%>
<%@page import="br.ufrn.sigaa.ensino.dominio.MetodoAvaliacao"%><f:view>
<h2><ufrn:subSistema /> &gt; Processo Seletivo e Avaliação do Aluno</h2>

<%@include file="include/_operacao.jsp"%>

<h:form id="form">
  <table class=formulario width="100%">
   <caption class="listagem">Dados do Processo Seletivo</caption>
	<tr>
		<td colspan="4">
			<table class="subFormulario" width="100%" >
			 <caption>Seleção</caption>
				<tr>
					<th width="45%">Forma de Seleção:</th>
					<td colspan="4">
					  <a4j:region>
						<h:selectManyCheckbox value="#{cursoLatoMBean.obj.formaSelecao}" id="selecaoProposta" layout="pageDirection">
							<f:selectItems value="#{cursoLatoMBean.formasSelecaoProposta}" />
						</h:selectManyCheckbox>
					  </a4j:region>
					</td>
				</tr>
			</table>	
		</td>
	</tr>
	<tr>
		<td colspan="4">
		  <table class="subFormulario" width="100%">
			<caption>Processo de Avaliação do desempenho do aluno no Curso</caption>
				<tr>
					<th width="45%">Formas de Avaliação: </th>
					<td colspan="4">
						<a4j:region>
							<h:selectManyCheckbox value="#{cursoLatoMBean.obj.formasAvaliacao}" id="formaAvaliacao" layout="pageDirection">
								<f:selectItems value="#{cursoLatoMBean.formasAvaliacaoProposta}" />
							</h:selectManyCheckbox>
						</a4j:region>
					</td>
				</tr>
		  </table>
		</td>
	</tr>
	<tr>
		<td colspan="4">
		  <table class="subFormulario" width="100%">
		  <caption> Média Aprovação </caption>
			<tr>
				<th class="obrigatorio">Nota mínima para aprovação:</th>
				<td width="50%">
					<h:inputText value="#{cursoLatoMBean.obj.propostaCurso.mediaMinimaAprovacao}" size="3" id="mediaMinima" title="mediaMinima" 
						onkeypress="return(formatarMascara(this,event,'#.#'))" maxlength="3" />
				</td>
			</tr>
		  </table>
		</td>
	</tr>
	<tr>
		<td colspan="4">
		  <table class="subFormulario" width="100%">
		  <caption> Frequencia </caption>
			<tr>
				<th class="obrigatorio">Frequencia Mínima Aprovação:</th>
				<td width="50%">
					<h:inputText value="#{cursoLatoMBean.obj.propostaCurso.freqObrigatoria}" size="3" id="minimaFrequencia" 
						title="minimaFrequencia" onkeypress="return(formatarMascara(this,event,'##.#'))" maxlength="4" />%
				</td>
			</tr>
		  </table>
		</td>
	</tr>
	  <tfoot>
		   <tr>
				<td colspan="2">
					<h:commandButton value="<< Voltar" action="#{cursoLatoMBean.telaAnterior}" id="voltar" />
					<h:commandButton value="Cancelar" action="#{cursoLatoMBean.cancelar}" onclick="#{confirm}" id="cancelar" />
					<h:commandButton value="Avançar >>" action="#{cursoLatoMBean.cadastrar}" id="cadastrar" />
				</td>
		   </tr>
		</tfoot>
   </table>
</h:form>
	<br />
	<center>
		<h:graphicImage url="/img/required.gif" style="vertical-align: top;" />
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>
	<br />
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>