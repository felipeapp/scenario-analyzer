<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<script type="text/javascript">

	function verificaAno(valor){
		var ano = parseInt(valor.value);
		if ( (valor.value).length == 4 && ano <= 2010 ){
			hideConvocacao();
		} else if ( (valor.value).length == 4 && ano > 2010 )	{
			showConvocacao();
		}	
	}
	
	function showConvocacao(){
		$('processo_seletivo').show();
		$('convocacao_processo_seletivo').show();
		$('chamada_codMerge').hide();
	}
	
	function hideConvocacao(){
		$('processo_seletivo').hide();
		$('convocacao_processo_seletivo').hide();
		$('chamada_codMerge').show();
	}
</script>	

<f:view>

<h2> <ufrn:subSistema/> > Relatório dos Alunos que Ingressaram em um Novo Curso</h2>

<h:form id="form">
<table align="center" class="formulario" width="70%">
	<caption class="listagem">Dados do Relatório</caption>

	<tr>
	    <th class="obrigatorio" width="20%">Ano-Período: </th>
		<td>
			<h:inputText id="ano" value="#{relatorioDiscente.ano}" size="4" maxlength="4" onkeyup="verificaAno(this); return formatarInteiro(this); "/>.
			<h:inputText id="periodo" value="#{relatorioDiscente.periodo}" size="1" maxlength="1"/>
		</td>
	</tr>
	
	<tr id="processo_seletivo" >
		<th width="20%" class="obrigatorio">Processo Seletivo: </th>
		<td>
			<h:selectOneMenu id="processoSeletivo" immediate="true" 
				value="#{relatorioDiscente.processoSeletivo}" onchange="submit()" 
				  valueChangeListener="#{documentosDiscentesConvocadosMBean.carregarChamadas}">
				<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
				<f:selectItems value="#{processoSeletivoVestibular.allAtivoCombo}" />
			</h:selectOneMenu>
		</td>
	</tr>
	<tr id="convocacao_processo_seletivo" >
		<th width="30%" class="obrigatorio">Chamada: </th>
		<td width="70%">
			<h:selectOneMenu id="chamada" value="#{relatorioDiscente.convocacao}">
				<f:selectItem itemValue="0" itemLabel="-- TODAS --" />
				<f:selectItems value="#{documentosDiscentesConvocadosMBean.fases}" />
			</h:selectOneMenu>
		</td>
	</tr>
	
	<tr id="chamada_codMerge" style="display:none;">
		<th width="30%">Chamada:</th>
		<td><h:selectOneMenu value="#{relatorioDiscente.chamada}" id="chamada_codMerge">
			<f:selectItem itemValue="0" itemLabel="-- TODAS --" />
			<f:selectItem itemValue="1" itemLabel="1ª Chamada" />
			<f:selectItem itemValue="2" itemLabel="2ª Chamada" />
			<f:selectItem itemValue="3" itemLabel="3ª Chamada" />
			<f:selectItem itemValue="4" itemLabel="4ª Chamada" />
			<f:selectItem itemValue="5" itemLabel="5ª Chamada" />
			<f:selectItem itemValue="6" itemLabel="6ª Chamada" />
			<f:selectItem itemValue="7" itemLabel="7ª Chamada" />
			<f:selectItem itemValue="8" itemLabel="8ª Chamada" />
			<f:selectItem itemValue="9" itemLabel="9ª Chamada" />
		</h:selectOneMenu></td>
		
	</tr>
	
	<tfoot>
  	  <tr>
		 <td colspan="3" align="center">
			<h:commandButton id="gerarRelatorio" value="Gerar Relatório"
				action="#{relatorioDiscente.alunosIngressantesOutroCurso}"/> 
			<h:commandButton value="Cancelar" action="#{relatorioDiscente.cancelar}" id="cancelar" 
				onclick="#{confirm}" />
		 </td>
	  </tr>
	</tfoot>
</table>
</h:form>

	<br>
	<center>
	<h:graphicImage url="/img/required.gif" style="vertical-align: top;"/>
	<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>
	<br>

</f:view>
		
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<script type="text/javascript">
	verificaAno(document.getElementById('form:ano'));
</script>
