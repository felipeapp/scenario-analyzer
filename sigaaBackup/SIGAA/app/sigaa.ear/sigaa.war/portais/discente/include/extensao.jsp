    <ul>
		 <li><h:commandLink action="#{ atividadeExtensao.preLocalizar }" value="Consultar A��es"  /> </li>
		 <li><h:commandLink action="#{ planoTrabalhoExtensao.carregarPlanosTrabalhoDiscenteLogado }" value="Meus Planos de Trabalho"  /> </li>
		 <li><h:commandLink action="#{ atividadeExtensao.carregarAcoesDiscenteLogado }" value="Minhas A��es como Membro da Equipe"  /> </li>
		 <li><h:commandLink action="#{ relatorioBolsistaExtensao.iniciarCadastroRelatorio }" value="Meus Relat�rios"  /> </li>
		 <li><h:commandLink action="#{ documentosAutenticadosExtensao.participacoesDiscenteUsuarioLogado }" value="Certificados e Declara��es"  /> </li>
		 <li><a href="${ctx}/public/extensao/consulta_inscricoes.jsf?aba=extensao">Inscri��o On-line em A��es de Extens�o</a></li>
		 <li><h:commandLink action="#{ selecaoDiscenteExtensao.iniciarVisualizarResultados }" value="Visualizar Resultados das inscri��es"  /> </li>
    </ul>