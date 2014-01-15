<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName="interesseOfertaMBean" />
<a4j:keepAlive beanName="ofertaEstagioMBean" />
<h2> <ufrn:subSistema /> &gt; Consultar Ofertas de Est�gio &gt; Inscrever em Processo Seletivo</h2>

<c:if test="${ interesseOfertaMBean.portalDiscente }">
	<div class="descricaoOperacao">
		<p>
			<b>ATEN��O!</b> Seus dados referentes a <b>e-mail</b>, <b>telefone</b>, <b>curso</b>,
				dentre outros, ser�o disponibilizados para a empresa que est�
				ofertando a vaga de est�gio para que, em um processo de sele��o
				de estagi�rios, possa eventualmente entrar em contato com voc�.
			</p>
		<p>Sugerimos que mantenha seus dados pessoais sempre
			atualizados.</p>
	</div>
</c:if>


<c:set var="oferta" value="#{interesseOfertaMBean.obj.oferta}"/>
<%@include file="/estagio/oferta_estagio/include/_oferta.jsp"%>

<c:set var="discente" value="#{interesseOfertaMBean.obj.discente.discente}"/>
<%@include file="/estagio/estagio/include/_discente.jsp"%>

<h:form enctype="multipart/form-data" id="form">
<table class="formulario" style="width: 90%">
	<tr>
		<td class="subFormulario" colspan="3">Descri��o do Perfil</td>		
	</tr>
	<tr>
		<th style="width: 30%;"></th>
		<td>
			<h:inputTextarea value="#{interesseOfertaMBean.obj.descricaoPerfil}" cols="80" rows="8"/>
		</td>
		<td style="vertical-align: middle; width: 15%; text-align: left;">
			<ufrn:help>Descreva seu perfil profissional ou dados que possam ser �teis para avalia��o.</ufrn:help>
		</td>
	</tr>
	<tr>
		<th>Curr�culo Lattes:</th>
		<td colspan="2">
		    <h:inputText value="#{interesseOfertaMBean.curriculoLattes}" size="80" maxlength="200" id="lattes"/> 
		    <ufrn:help>Informe o link do seu Curr�culo Lattes.</ufrn:help>		
		</td>
	</tr>
	<tr>
		<th>Enviar Arquivo:</th>
		<td>
			<t:inputFileUpload value="#{interesseOfertaMBean.obj.arquivoCurriculo}" styleClass="file" id="nomeArquivo" />
			<ufrn:help>Voc� poder� um arquivo complementar como, por exemplo, um hist�rico ou um curr�culo personalizado. Opte em enviar no formato PDF, que possui maior portabilidade para leitura.</ufrn:help>
		</td>
	</tr>	
	<tfoot>
		<tr>
			<td colspan="3">
				<h:commandButton value="Cadastrar" action="#{interesseOfertaMBean.cadastrar}" id="btCadastrar"/>
				<h:commandButton value="<< Voltar" rendered="#{ofertaEstagioMBean.portalDiscente}" action="#{ofertaEstagioMBean.listarOfertasDisponiveis}" id="btVoltarDiscente"/>
				<h:commandButton value="<< Voltar" rendered="#{!ofertaEstagioMBean.portalDiscente}" action="#{interesseOfertaMBean.informaDiscente}" id="btVoltar"/>
				<h:commandButton value="Cancelar" action="#{interesseOfertaMBean.cancelar}" onclick="#{confirm}" immediate="true" id="btCancel"/>
			</td>
		</tr>
	</tfoot>
</table>
</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>