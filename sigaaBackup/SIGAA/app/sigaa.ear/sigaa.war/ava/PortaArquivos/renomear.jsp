
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	
	<h:form>
	<%@include file="/portais/turma/menu_turma.jsp"%>
	</h:form>
	
	<h2>Porta Arquivos</h2>
	
	<h:form>
	<h:messages showDetail="true"/>
	
	<table class="formulario" width="70%">

			<caption>Renomear Arquivo</caption>
			<tr>
				<th>&nbsp;&nbsp;Arquivo:&nbsp;</th>
				<td><t:inputText value="#{arquivoUsuario.obj.nome}" size="50"/></td>
			</tr>

			<tfoot>
				<tr>
					<td colspan="2">
						<h:inputHidden value="#{ arquivoUsuario.obj.id }"/>
						<h:commandButton value="Renomear" action="#{arquivoUsuario.renomear}" /> 
						<h:commandButton value="Cancelar" action="#{arquivoUsuario.cancelar}"/>
					</td>
				</tr>
			</tfoot>

		</table>
	</h:form>
		
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
