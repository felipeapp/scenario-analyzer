<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
	table.listagem tr td.periodo {
		background: #C4D2EB;
		padding: 3px;
		font-weight: bold;
	}

</style>

<f:view>

<h2><ufrn:subSistema/> > Publicar Turma Virtual para comunidade externa</h2>

<style>
	table#cursosAbertos{margin:auto;}
	table#cursosAbertos label{padding-right:15px;}
	.formulario tfoot tr {background: #C8D5EC;}
</style>
<a4j:keepAlive beanName="configuracoesAva"></a4j:keepAlive>
<h:form id="form">
	<%--
		Exibe o no formul�rio de configura��es somente a op��o de acesso p�blico, quando usu�rio acessar a 
		op��o Configurar > Publicar Turma
	--%>
	<div class="descricaoOperacao">
			<p>Essa op��o permite ao docente tornar p�blica a turma virtual, atrav�s do <a href="${ ctx }/public/curso_aberto/portal.jsf?aba=p-ensino" target="_blank" title="Acessar Portal P�blico dos Cursos Abertos">
				Portal P�blico dos Cursos Abertos</a>, onde qualquer pessoa poder� visualizar <b>somente</b> os t�picos de aulas e materiais relacionados.
			</p>
			<p>
				Caso concorde, a turma virtual ser� disponibilizada neste portal para toda internet ter acesso, caso contr�rio, ser� despublicada se estiver publicada. 
			</p>
	</div>	

	<table id="cursosAbertos" class="formulario">
		<caption>Publica��o de Turma Virtual</caption>
		<tbody>
			<tr>
				<td>
					Publicar Turma Virtual ? &nbsp;
				</td>
				<td>
					<h:selectOneRadio id="permitePublicar" value="#{ configuracoesAva.config.permiteVisualizacaoExterna }">
						<f:selectItem  itemLabel=" Sim " itemValue="#{true}"   />
						<f:selectItem itemLabel=" N�o " itemValue="#{false}"/>
					</h:selectOneRadio>
					<h:inputHidden value="#{ configuracoesAva.opcaoCursosAbertos}"/>
				</td>
			</tr>
		</tbody>			
		<tfoot>
			<tr>
				<td align="center" colspan="2">
					<h:inputHidden value="#{ configuracoesAva.config.id }"/>
					<h:commandButton action="#{configuracoesAva.salvar}" value="Salvar" />&nbsp;
					<h:commandButton action="#{configuracoesAva.cancelar}" value="Cancelar" onclick="#{confirm}" />
				</td>
			</tr>
		</tfoot>
	</table>	
	<br/>

</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
