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
		Exibe o no formulário de configurações somente a opção de acesso público, quando usuário acessar a 
		opção Configurar > Publicar Turma
	--%>
	<div class="descricaoOperacao">
			<p>Essa opção permite ao docente tornar pública a turma virtual, através do <a href="${ ctx }/public/curso_aberto/portal.jsf?aba=p-ensino" target="_blank" title="Acessar Portal Pùblico dos Cursos Abertos">
				Portal Público dos Cursos Abertos</a>, onde qualquer pessoa poderá visualizar <b>somente</b> os tópicos de aulas e materiais relacionados.
			</p>
			<p>
				Caso concorde, a turma virtual será disponibilizada neste portal para toda internet ter acesso, caso contrário, será despublicada se estiver publicada. 
			</p>
	</div>	

	<table id="cursosAbertos" class="formulario">
		<caption>Publicação de Turma Virtual</caption>
		<tbody>
			<tr>
				<td>
					Publicar Turma Virtual ? &nbsp;
				</td>
				<td>
					<h:selectOneRadio id="permitePublicar" value="#{ configuracoesAva.config.permiteVisualizacaoExterna }">
						<f:selectItem  itemLabel=" Sim " itemValue="#{true}"   />
						<f:selectItem itemLabel=" Não " itemValue="#{false}"/>
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
