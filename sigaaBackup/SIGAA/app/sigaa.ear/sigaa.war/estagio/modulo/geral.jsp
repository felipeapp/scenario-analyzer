<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<ul>
	<ufrn:checkRole papeis="<%= new int[] {	SigaaPapeis.GESTOR_CONVENIOS_ESTAGIO_PROPLAN} %>">
	<li>Convênios de Estágio
		<ul>
			<li> <h:commandLink action="#{convenioEstagioMBean.iniciar}" value="Cadastrar Convênio de Estágio" onclick="setAba('geral')" /> </li>
			<li> <h:commandLink action="#{convenioEstagioMBean.iniciarConsulta}" value="Listar/Alterar Convênios de Estágio" onclick="setAba('geral')" /> </li>
			<li> 
				<h:commandLink action="#{convenioEstagioMBean.iniciarAnalise}" value="Analisar Convênios de Estágio Submetidos" onclick="setAba('geral')" /> 
				<h:outputText value=" (#{ moduloConvenioEstagioMBean.qtdConveniosPendentesAnalise })" rendered="#{ moduloConvenioEstagioMBean.qtdConveniosPendentesAnalise > 0}"
					style="color: red;"/> 
			</li>
		</ul>
	</li>
	<li>Oferta de Estágio
		<ul>
			<li> <h:commandLink action="#{ofertaEstagioMBean.iniciar}" value="Cadastrar Oferta de Estágio" onclick="setAba('geral')" /> </li>			
			<li> <h:commandLink action="#{ofertaEstagioMBean.iniciarConsulta}" value="Listar/Alterar Oferta de Estágio" onclick="setAba('geral')" /> </li>				
			<li> <h:commandLink action="#{buscaEstagioMBean.iniciar}" value="Gerenciar Estagiários" onclick="setAba('geral')" /> </li>
			<li> <h:commandLink action="#{estagioMBean.iniciarCadastroAvulso}" value="Cadastrar Estagiários Avulso" onclick="setAba('geral')" /> </li>
		</ul>
	</li>
	<li>Relatórios de Estágio
		<ul>
			<li> <h:commandLink action="#{questionarioBean.iniciarCadastroEstagio}" value="Cadastrar" onclick="setAba('geral')" /> </li>
			<li> <h:commandLink action="#{questionarioBean.gerenciarQuestionarioEstagio}" value="Listar/Alterar" onclick="setAba('geral')" /> </li>
		</ul>
	</li>
	</ufrn:checkRole>
</ul>