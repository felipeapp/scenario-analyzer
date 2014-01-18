<%-- MENU DE OPÇÕES PARA O DOCENTE --%>
<%@taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@taglib uri="http://java.sun.com/jsf/core"  prefix="f" %>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<div id="menu-dropdown">
	<div class="wrapper">
		<h:form>
			<input type="hidden" name="jscook_action"/>	
				<t:jscookMenu layout="hbr" theme="ThemeOffice" styleLocation="/css/jscookmenu">
					<t:navigationMenuItem itemLabel="Turmas" icon="/img/group.png">				
						<t:navigationMenuItem itemLabel="Lançar Frequência" action="#{lancamentoFrequenciaIMD.redirecionarSelecaoTurmaLancarFreq}" itemDisabled="false" />															</t:navigationMenuItem>				
				</t:jscookMenu>
		</h:form>
	</div>
</div>