<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<script type="text/javascript">
<!--
 function mostraPerfilTecCoordenador(obj) {
	if(obj.value == 'true'){ 
		$('idSoTecNivSup').style.display = '';
	}else {
		$('idSoTecNivSup').style.display = 'none';
	}
 }

//-->
</script>

<f:view>
<h2> <ufrn:subSistema /> &gt; Edital</h2>
<!--<h:messages showDetail="true"/>-->
<h:form enctype="multipart/form-data" id="form">
<table class="formulario">
<caption>Cadastro de Editais</caption>
<tr>
	<th class="required"> Descri��o: </th>
	<td> <h:inputText value="#{ editalMBean.obj.descricao }" id="descricao" size="80" maxlength="90" />
</td>
</tr>
<tr>
	<th class="required"> In�cio do per�odo de submiss�es: </th>
	<td> 
		<t:inputCalendar id="inicioPeriodoSub" title="In�cio do per�odo de submiss�es" value="#{ editalMBean.obj.inicioSubmissao }" 
		renderAsPopup="true" renderPopupButtonAsImage="true" onkeypress="return(formataData(this,event))" 
		size="10" maxlength="10" popupDateFormat="dd/MM/yyyy"/>
	</td>
</tr>
<tr>
	<th class="required"> Fim do per�odo de submiss�es: </th>
	<td> 
		<t:inputCalendar id="fimPeriodoSub" title="Fim do per�odo de submiss�es" value="#{ editalMBean.obj.fimSubmissao }" 
		renderAsPopup="true" renderPopupButtonAsImage="true" onkeypress="return(formataData(this,event))" 
		size="10" maxlength="10" popupDateFormat="dd/MM/yyyy" /> 
	</td>
</tr>
<tr>
	<th class="required"> In�cio do per�odo de realiza��o dos projetos: </th>
	<td> 
		<t:inputCalendar id="inicioPeriodoRealiProj" title="In�cio do per�odo de realiza��o dos projetos" value="#{ editalMBean.obj.inicioRealizacao }" 
		renderAsPopup="true" renderPopupButtonAsImage="true" onkeypress="return(formataData(this,event))" 
		size="10" maxlength="10" popupDateFormat="dd/MM/yyyy"/> 
		
	</td>
</tr>
<tr>
	<th class="required"> Fim do per�odo de realiza��o dos projetos: </th>
	<td> 
		<t:inputCalendar id="fimPeriodoRealiProj" title="Fim do per�odo de realiza��o dos projetos" value="#{ editalMBean.obj.fimRealizacao }" 
		renderAsPopup="true"  renderPopupButtonAsImage="true" onkeypress="return(formataData(this,event))" 
		size="10" maxlength="10" popupDateFormat="dd/MM/yyyy"/> 
	</td>
</tr>
<tr>
	<th class="required"> In�cio do prazo para reconsidera��o de avalia��es: </th>
	<td> 
		<t:inputCalendar id="inicioPeriodoRecoAval" title="In�cio do prazo para reconsidera��o de avalia��es" value="#{ editalMBean.obj.dataInicioReconsideracao }" 
		renderAsPopup="true" renderPopupButtonAsImage="true" onkeypress="return(formataData(this,event))" 
		size="10" maxlength="10" popupDateFormat="dd/MM/yyyy"/> 
	</td>
</tr>
<tr>
	<th class="required"> Fim do prazo para reconsidera��o de avalia��es:</th>
	<td> 
		<t:inputCalendar id="fimPeriodoRecoAval" title="Fim do prazo para reconsidera��o de avalia��es" value="#{ editalMBean.obj.dataFimReconsideracao }" 
		renderAsPopup="true"  renderPopupButtonAsImage="true" onkeypress="return(formataData(this,event))" 
		size="10" maxlength="10" popupDateFormat="dd/MM/yyyy"/> 
	</td>
</tr>
<tr>
	<th class="required"> Ano-Semestre: </th>
	<td> 
		<h:inputText id="ano" value="#{ editalMBean.obj.ano }" size="4" maxlength="4" onkeyup="formatarInteiro(this);"/> -
		<h:inputText id="periodo" value="#{ editalMBean.obj.semestre }" size="1"maxlength="1" onkeyup="formatarInteiro(this);"/>
	</td>
</tr>

<tr>
    <th class="required">Or�amento m�ximo para 1 dimens�o acad�mica(R$):</th>
    <td>
        <h:inputText id="primeiroLimiteOrcamentario" value="#{editalMBean.obj.primeiroLimiteOrcamentario}" size="12" maxlength="10" 
        	onkeypress="return(formataValor(this, event, 2))" readonly="#{editalMBean.readOnly}" label="Or�amento m�ximo para 1 dimens�o acad�mica">
            <f:convertNumber pattern="#,##0.00" />
        </h:inputText>
    </td>
</tr>


<tr>
    <th class="required">Or�amento m�ximo para 2 dimens�es acad�micas(R$):</th>
    <td>
        <h:inputText id="segundoLimiteOrcamentario" value="#{editalMBean.obj.segundoLimiteOrcamentario}" size="12" maxlength="10" 
        	onkeypress="return(formataValor(this, event, 2))" readonly="#{editalMBean.readOnly}" label="Or�amento m�ximo para 2 dimens�es acad�micas">
            <f:convertNumber pattern="#,##0.00"/>
        </h:inputText>
    </td>
</tr>

<tr>
    <th class="required">Or�amento m�ximo para 3 dimens�es acad�micas(R$):</th>
    <td>
        <h:inputText id="terceiroLimiteOrcamentario" value="#{editalMBean.obj.terceiroLimiteOrcamentario}" size="12" maxlength="10" 
        	onkeypress="return(formataValor(this, event, 2))" readonly="#{editalMBean.readOnly}" label="Or�amento m�ximo para 3 dimens�es acad�micas">
            <f:convertNumber pattern="#,##0.00"/>
        </h:inputText>
    </td>
</tr>

<tr>
	<h:panelGroup rendered="#{editalMBean.obj.id == 0}" >
		<th class="required">Arquivo do Edital: </th>
	</h:panelGroup>
	<h:panelGroup rendered="#{editalMBean.obj.id>0}" >
		<th>Arquivo do Edital: </th>
	</h:panelGroup>
	
	 <td>
		<t:inputFileUpload id="uFile" value="#{editalMBean.file}" storage="file" size="70"/>
	</td>
</tr>


<tr>
    <td class="subFormulario" colspan="2">Restri��es de coordena��o</td>
</tr>

<tr>
    <th class="required">M�ximo de coordena��es ativas por usu�rio neste edital:</th>
    <td>
        <h:inputText id="maxCoordenacoesAtivas" label="M�ximo de coordena��es ativas por usu�rio neste edital" value="#{editalMBean.obj.restricaoCoordenacao.maxCoordenacoesAtivas}" size="12" maxlength="10" onkeyup="formatarInteiro(this);" readonly="#{editalMBean.readOnly}" />
    </td>
</tr>


<tr>
    <th class="required">Somente servidores do quadro e em efetivo exerc�cio podem coordenar:</th>
    <td>
        <h:selectOneRadio id="apenasServidorAtivoCoordena" value="#{editalMBean.obj.restricaoCoordenacao.apenasServidorAtivoCoordena}">
            <f:selectItem itemLabel="SIM" itemValue="true"/>
		    <f:selectItem itemLabel="N�O" itemValue="false"/>           
        </h:selectOneRadio>        
    </td>
</tr>


<tr>
    <th class="required">Permitir docentes como coordenadores:</th>
    <td>
        <h:selectOneRadio id="permitirCoordenadorDocente" value="#{editalMBean.obj.restricaoCoordenacao.permitirCoordenadorDocente}" layout="lineDirection">
	        <f:selectItem itemLabel="SIM" itemValue="true"/>
		    <f:selectItem itemLabel="N�O" itemValue="false"/>           
        </h:selectOneRadio>
    </td>
</tr>

<tr>
    <th class="required">Permitir t�cnicos administrativos como coordenadores:</th>
    <td>
        <h:selectOneRadio id="permitirCoordenadorTecnico" value="#{editalMBean.obj.restricaoCoordenacao.permitirCoordenadorTecnico}" layout="lineDirection"
        	onclick="javascript:mostraPerfilTecCoordenador(this);" >
   	        <f:selectItem itemLabel="SIM" itemValue="true"/>
		    <f:selectItem itemLabel="N�O" itemValue="false"/>
        </h:selectOneRadio>
        
    </td>
</tr>

<tr id="idSoTecNivSup" style="display: ${editalMBean.obj.restricaoCoordenacao.permitirCoordenadorTecnico ? '' : 'none'}">
    <th class="required">Perfil dos t�cnicos administrativos como coordenadores:</th>
    <td>
        <h:selectOneRadio id="apenasTecnicoSuperiorCoordena" value="#{editalMBean.obj.restricaoCoordenacao.apenasTecnicoSuperiorCoordena}">
            <f:selectItem itemLabel="SOMENTE COM N�VEL SUPERIOR" itemValue="true"/>
		    <f:selectItem itemLabel="QUALQUER T�CNICO" itemValue="false"/>           
        </h:selectOneRadio>
        
    </td>
</tr>

<tfoot>
<tr>
<td colspan="2">
    <h:inputHidden value="#{editalMBean.obj.id}"/>
    <h:commandButton value="#{editalMBean.confirmButton}" action="#{editalMBean.cadastrar}"/>
    <a4j:region rendered="#{ editalMBean.alterar}">
    	<h:commandButton value="Cancelar" action="#{editalMBean.listar}" immediate="true" onclick="#{confirm}"/>
    </a4j:region>
    <a4j:region rendered="#{ not editalMBean.alterar}">
    	<h:commandButton value="Cancelar" action="#{editalMBean.cancelar}" onclick="#{confirm}" immediate="true"/>
    </a4j:region>
</td>
</tr>
</tfoot>
</table>

 <br />
 <center>
 <h:graphicImage url="/img/required.gif" style="vertical-align: top;"/>
 <span class="fontePequena"> Campos de preenchimento obrigat�rio. </span>
 </center>

</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
