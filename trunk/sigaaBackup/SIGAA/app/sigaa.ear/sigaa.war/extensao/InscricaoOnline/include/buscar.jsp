<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>
<a4j:keepAlive beanName="inscricaoParticipantes" />
<h:form id="frmBuscar">
    <table class="formulario" width="60%">
    
        <caption>Busca por Ações de Extensão</caption>
        <tbody>
            <tr>
                <td width="3%" align="center">
                    <h:selectBooleanCheckbox value="#{inscricaoParticipantes.checkBuscaTitulo}" id="selectBuscaTitulo" styleClass="noborder" /> 
                </td>
                <td width="17%">
                    <label for="frmBuscar:selectBuscaTitulo">Título da Ação:</label>
                </td>
                <td><h:inputText id="buscaTitulo" value="#{inscricaoParticipantes.buscaNomeAtividade}" size="50" 
                            onchange="javascript:$('frmBuscar:selectBuscaTitulo').checked = true;" /></td>
           </tr>

            <tr>
                <td align="center">
                    <h:selectBooleanCheckbox value="#{inscricaoParticipantes.checkBuscaTipoAtividade}" 
                            id="selectBuscaTipoAtividade" styleClass="noborder" /> 
                    </td>
                <td><label for="frmBuscar:selectBuscaTipoAtividade">Tipo de Atividade:</label></td>
                <td>            
                    <h:selectOneMenu id="buscaTipoAcao" value="#{inscricaoParticipantes.buscaTipoAtividade}" 
                            onchange="javascript:$('frmBuscar:selectBuscaTipoAtividade').checked = true;">
                        <f:selectItem itemLabel="TODOS" itemValue="0" />
                        <f:selectItems value="#{tipoAtividadeExtensao.cursoEventoCombo}" />
                    </h:selectOneMenu>           
                </td>
            </tr>

            <tr>    
                <td align="center"> 
                    <h:selectBooleanCheckbox value="#{inscricaoParticipantes.checkBuscaArea}" id="selectBuscaArea" styleClass="noborder" /> 
                </td>
                <td><label for="frmBuscar:selectBuscaArea">Área Temática:</label></td>
                <td>
                    <h:selectOneMenu id="buscaArea" value="#{inscricaoParticipantes.buscaArea}" 
                            onchange="javascript:$('frmBuscar:selectBuscaArea').checked = true;">
                        <f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
                        <f:selectItems value="#{areaTematica.allCombo}" />
                    </h:selectOneMenu>
                 </td>
            </tr>
        
            <tr>
                <td align="center"> 
                    <h:selectBooleanCheckbox value="#{inscricaoParticipantes.checkBuscaServidor}" 
                            id="selectBuscaServidor" styleClass="noborder" />  
                </td>
                <td><label for="frmBuscar:selectBuscaServidor">Coordenador:</label></td>
                <td>
                    <h:inputHidden id="buscaServidor" value="#{inscricaoParticipantes.docente.id}" />
                    <h:inputText id="buscaNome" value="#{inscricaoParticipantes.docente.pessoa.nome}" 
                            size="50" onchange="javascript:$('frmBuscar:selectBuscaServidor').checked = true;" /> 
                            
                    <ajax:autocomplete source="frmBuscar:buscaNome" target="frmBuscar:buscaServidor" minimumCharacters="3"
                            baseUrl="/sigaa/ajaxServidor" className="autocomplete" indicator="indicator"  
                            parameters="tipo=todos,inativos=false,areaPublica=true" parser="new ResponseXmlToHtmlListParser()" /> 
                    <span id="indicator" style="display:none;"> 
                        <img src="/sigaa/img/indicator.gif" /> 
                    </span>
                </td>
            </tr>
            
            <tr>
                <td align="center"> 
                    <h:selectBooleanCheckbox value="#{inscricaoParticipantes.checkBuscaPeriodo}" id="selectBuscaPeriodo" styleClass="noborder" /> 
                </td>
                <td><label for="frmBuscar:selectBuscaPeriodo">Período:</label></td>
                <td><t:inputCalendar id="periodoInicio" value="#{inscricaoParticipantes.buscaInicio}" renderAsPopup="true" 
                            renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" size="10" maxlength="10"
                            popupTodayString="Hoje é" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
                            onchange="javascript:$('frmBuscar:selectBuscaPeriodo').checked = true;">
                        <f:converter converterId="convertData" />
                    </t:inputCalendar> 
                    <i>até</i> 
                    <t:inputCalendar id="periodoFim" value="#{inscricaoParticipantes.buscaFim}" renderAsPopup="true" 
                            renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" size="10" maxlength="10"
                            popupTodayString="Hoje é" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
                            onchange="javascript:$('frmBuscar:selectBuscaPeriodo').checked = true;">
                        <f:converter converterId="convertData" />
                    </t:inputCalendar></td>
            </tr> 
            <tr>
                <td>
                    <h:selectBooleanCheckbox value="#{inscricaoParticipantes.checkBuscaAbertas}" id="selectBuscaAbertas" styleClass="noborder" />
                </td>
                <td colspan="2">
                    <label for="frmBuscar:selectBuscaAbertas">Apenas Inscrições Abertas</label>
                    <ufrn:help>Caso está opção esteja desmarcada, a busca trará todas as inscrições existentes.</ufrn:help>
                </td>
            </tr>
        </tbody>
        <tfoot>
            <tr>
                <td colspan="3">
                    <h:commandButton action="#{inscricaoParticipantes.buscarAcoesComInscricoes}" value="Buscar" id="tbnBuscar" />
                    <h:commandButton action="#{inscricaoParticipantes.cancelar}" value="Cancelar" id="btnCancelar" />
                </td>
            </tr>
        </tfoot>
    </table>

</h:form>

<c:set var="inscricoes" value="#{inscricaoParticipantes.inscricoesAtividades}"/>
<c:set var="inscricoesSubAtividades" value="#{inscricaoParticipantes.inscricoesSubAtividades}"/>

<br/>